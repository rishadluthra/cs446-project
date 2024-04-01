import { Controller, Post, Body } from '@nestjs/common';
import { EmailService } from './email.service';
import { CurrentUser } from '../decorators/user.decorator';


@Controller('email')
export class EmailController {
  constructor(private readonly emailService: EmailService) {}

  @Post('send')
  async sendEmail(
    @Body() { email: String }
  ): Promise<String> {
    try {
      const code = await this.emailService.sendEmail("beaconsinfo10@gmail.com");
      return code.toString();
    } catch (error) {
      return error;
    }
  }
}