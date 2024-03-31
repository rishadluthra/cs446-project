import { Injectable } from '@nestjs/common';
import * as nodemailer from 'nodemailer';


@Injectable()
export class EmailService {
  private transporter;

    constructor() {
      this.transporter = nodemailer.createTransport({
        service: 'Gmail',
        auth: {
          user: 'beaconsinfo10@gmail.com',
          pass: 'xnio ajlk pqyw rzxu',
        },
      });
    }



    async sendEmail(to: string): Promise<String | null> {
        let min = 100000;
        let max = 999999;
        const code = Math.floor(Math .random() * (max - min + 1)) + min
        const mailOptions = {
            from: 'beaconsinfo10@gmail.com',
            to: to,
            subject: 'Beacon Verification Code',
            text: 'Here is the verification code for your Beacon Account: ' + code.toString(),
        };

        try {
            await this.transporter.sendMail(mailOptions);
            console.log('Email sent successfully');
            return code.toString()
        } catch (error) {
            console.error('Error sending email:', error);
            throw error;
            return null
        }
    }
}
