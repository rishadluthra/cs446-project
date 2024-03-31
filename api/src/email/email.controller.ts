import { Controller, Get } from '@nestjs/common';
import { EmailService } from './email.service';
import { CurrentUser } from '../decorators/user.decorator';


@Controller('email')
export class EmailController {
  constructor(private readonly emailService: EmailService) {}

  @Get('send')
  async sendEmail(
  ): Promise<String> {
    try {
      const code = await this.emailService.sendEmail("beaconsinfo10@gmail.com");
      return code;
    } catch (error) {
      return error;
    }
  }
}