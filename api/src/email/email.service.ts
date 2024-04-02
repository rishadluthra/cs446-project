import { Injectable } from '@nestjs/common';
import { createTransport, Transporter } from 'nodemailer';

import { EmailDto } from './dto/email.dto';

@Injectable()
export class EmailService {
  private transporter: Transporter;

  constructor() {
    this.transporter = createTransport({
      service: 'Gmail',
      auth: {
        user: process.env.EMAIL_ADDRESS,
        pass: process.env.EMAIL_PASSWORD,
      },
    });
  }

  async sendEmail(
    recipient: string,
    { subject, text }: EmailDto,
  ): Promise<void> {
    const mailOptions = {
      from: process.env.EMAIL_ADDRESS,
      to: recipient,
      subject,
      text,
    };

    try {
      const code = await this.transporter.sendMail(mailOptions);
      return code;
    } catch (error) {
      throw error;
    }
  }
}
