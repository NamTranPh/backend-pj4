package com.example.backend_pj4.infrastructure.mail;

import java.time.Year;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.OtpSender;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.infrastructure.config.properties.MailProperties;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailOtpSender implements OtpSender {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public EmailOtpSender(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    public void send(String email, String otpCode, OtpType type) {
        String appName = mailProperties.getFromName() != null ? mailProperties.getFromName() : "System";
        String supportEmail = mailProperties.getFromAddress() != null ? mailProperties.getFromAddress() : "support@localhost";
        int year = Year.now().getValue();

        String subjectText = switch (type) {
            case REGISTRATION -> "Xác nhận đăng ký tài khoản";
            case PASSWORD_RESET -> "Đặt lại mật khẩu";
        };
        
        String subject = String.format("[%s] %s", appName, subjectText);

        String body;
        switch (type) {
            case REGISTRATION:
                body = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Xác thực tài khoản</title>
                </head>
                <body style="margin:0;padding:0;background:#f4f5f7;font-family:Arial,sans-serif;">
                  <table width="100%" cellpadding="0" cellspacing="0" style="background:#f4f5f7;padding:40px 16px;">
                    <tr>
                      <td align="center">
                        <table width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #e0e0e0;">
                          <!-- Header -->
                          <tr>
                            <td style="background:#185FA5;padding:28px 40px;text-align:center;">
                              <span style="font-size:22px;font-weight:bold;color:#ffffff;letter-spacing:0.5px;">%s</span>
                            </td>
                          </tr>
                          <!-- Body -->
                          <tr>
                            <td style="padding:36px 40px;">
                              <h2 style="margin:0 0 8px;font-size:22px;font-weight:600;color:#1a1a1a;">
                                Xác thực tài khoản của bạn
                              </h2>
                              <p style="margin:0 0 28px;font-size:15px;color:#555;line-height:1.7;">
                                Chúng tôi nhận được yêu cầu xác thực từ tài khoản của bạn.
                                Sử dụng mã OTP dưới đây để tiếp tục đăng ký.
                              </p>
                              <!-- OTP Box -->
                              <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom:24px;">
                                <tr>
                                  <td style="background:#f0f4f9;border:1px solid #d0dce8;border-radius:10px;padding:24px;text-align:center;">
                                    <p style="margin:0 0 8px;font-size:12px;color:#888;letter-spacing:1.5px;text-transform:uppercase;">
                                      Mã xác thực OTP
                                    </p>
                                    <p style="margin:0;font-size:38px;font-weight:bold;letter-spacing:12px;color:#185FA5;font-family:monospace;">
                                      %s
                                    </p>
                                    <p style="margin:12px 0 0;font-size:13px;color:#999;">
                                      Hiệu lực trong <strong style="color:#555;">5 phút</strong>
                                    </p>
                                  </td>
                                </tr>
                              </table>
                              <!-- Warning -->
                              <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom:28px;">
                                <tr>
                                  <td style="background:#fff8ec;border-left:3px solid #f0a500;border-radius:4px;padding:14px 16px;">
                                    <p style="margin:0;font-size:13px;color:#7a5200;line-height:1.6;">
                                      <strong>Lưu ý bảo mật:</strong> Không chia sẻ mã này với bất kỳ ai,
                                      kể cả nhân viên hỗ trợ của chúng tôi.
                                    </p>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:0;font-size:14px;color:#888;line-height:1.7;">
                                Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này
                                hoặc <a href="mailto:%s" style="color:#185FA5;text-decoration:none;">liên hệ hỗ trợ</a>
                                nếu bạn lo ngại về bảo mật tài khoản.
                              </p>
                            </td>
                          </tr>
                          <!-- Footer -->
                          <tr>
                            <td style="border-top:1px solid #eeeeee;padding:20px 40px;text-align:center;">
                              <p style="margin:0 0 4px;font-size:12px;color:#aaa;">
                                © %d %s. Đây là email tự động, vui lòng không phản hồi.
                              </p>
                              <p style="margin:0;font-size:12px;color:#aaa;">
                                Nếu cần hỗ trợ, liên hệ
                                <a href="mailto:%s" style="color:#185FA5;text-decoration:none;">%s</a>
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(appName, otpCode, supportEmail, year, appName, supportEmail, supportEmail);
                break;
            
            case PASSWORD_RESET:
                body = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Đặt lại mật khẩu</title>
                </head>
                <body style="margin:0;padding:0;background:#f4f5f7;font-family:Arial,sans-serif;">
                  <table width="100%" cellpadding="0" cellspacing="0" style="background:#f4f5f7;padding:40px 16px;">
                    <tr>
                      <td align="center">
                        <table width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #e0e0e0;">
                          <!-- Header -->
                          <tr>
                            <td style="background:#185FA5;padding:28px 40px;text-align:center;">
                              <span style="font-size:22px;font-weight:bold;color:#ffffff;letter-spacing:0.5px;">%s</span>
                            </td>
                          </tr>
                          <!-- Body -->
                          <tr>
                            <td style="padding:36px 40px;">
                              <h2 style="margin:0 0 8px;font-size:22px;font-weight:600;color:#1a1a1a;">
                                Đặt lại mật khẩu
                              </h2>
                              <p style="margin:0 0 28px;font-size:15px;color:#555;line-height:1.7;">
                                Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.
                                Sử dụng mã OTP dưới đây để tiếp tục. Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này.
                              </p>
                              <!-- OTP Box -->
                              <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom:24px;">
                                <tr>
                                  <td style="background:#fff4f4;border:1px solid #f5c6c6;border-radius:10px;padding:24px;text-align:center;">
                                    <p style="margin:0 0 8px;font-size:12px;color:#888;letter-spacing:1.5px;text-transform:uppercase;">
                                      Mã xác thực đặt lại mật khẩu
                                    </p>
                                    <p style="margin:0;font-size:38px;font-weight:bold;letter-spacing:12px;color:#d9534f;font-family:monospace;">
                                      %s
                                    </p>
                                    <p style="margin:12px 0 0;font-size:13px;color:#999;">
                                      Hiệu lực trong <strong style="color:#555;">5 phút</strong>
                                    </p>
                                  </td>
                                </tr>
                              </table>
                              <!-- Warning -->
                              <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom:28px;">
                                <tr>
                                  <td style="background:#fff8ec;border-left:3px solid #f0a500;border-radius:4px;padding:14px 16px;">
                                    <p style="margin:0;font-size:13px;color:#7a5200;line-height:1.6;">
                                      <strong>Lưu ý bảo mật:</strong> Không chia sẻ mã này với bất kỳ ai,
                                      kể cả nhân viên hỗ trợ của chúng tôi. Mã chỉ có hiệu lực 1 lần.
                                    </p>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:0;font-size:14px;color:#888;line-height:1.7;">
                                Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này
                                hoặc <a href="mailto:%s" style="color:#185FA5;text-decoration:none;">liên hệ hỗ trợ</a>
                                ngay nếu bạn lo ngại về bảo mật tài khoản.
                              </p>
                            </td>
                          </tr>
                          <!-- Footer -->
                          <tr>
                            <td style="border-top:1px solid #eeeeee;padding:20px 40px;text-align:center;">
                              <p style="margin:0 0 4px;font-size:12px;color:#aaa;">
                                © %d %s. Đây là email tự động, vui lòng không phản hồi.
                              </p>
                              <p style="margin:0;font-size:12px;color:#aaa;">
                                Nếu cần hỗ trợ, liên hệ
                                <a href="mailto:%s" style="color:#185FA5;text-decoration:none;">%s</a>
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(appName, otpCode, supportEmail, year, appName, supportEmail, supportEmail);
                break;
            default:
                throw new IllegalArgumentException("Unknown OTP type: " + type);
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(supportEmail, appName);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("OTP email sent to={} type={}", email, type);
        } catch (Exception e) {
            log.error("Failed to send OTP email to={} type={}", email, type, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
