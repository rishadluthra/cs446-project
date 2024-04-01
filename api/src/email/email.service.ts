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
        user: 'beaconsinfo10@gmail.com',
        pass: 'xnio ajlk pqyw rzxu',
      },
    });
  }

  async sendEmail(
    recipient: string,
    { subject, text }: EmailDto,
  ): Promise<void> {
    const mailOptions = {
      from: 'beaconsinfo10@gmail.com',
      to: recipient,
      subject,
      text,
    };

    try {
      return this.transporter.sendMail(mailOptions);
    } catch (error) {
      throw error;
    }
  }
}
