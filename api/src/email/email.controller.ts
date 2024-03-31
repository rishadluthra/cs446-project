import { Controller, Get } from '@nestjs/common';
import { EmailService } from './email.service';

import { CurrentUser } from '../decorators/user.decorator';
import { User } from '../users/user.schema';


@Controller('email')
export class EmailController {
  constructor(private readonly emailService: EmailService) {}

  @Get('send')
  async sendEmail(
       @CurrentUser() currentUser: Partial<User>
  ): Promise<String> {
    try {
      const code = await this.emailService.sendEmail("beaconsinfo10@gmail.com");
      return code;
    } catch (error) {
      return error;
    }
  }
}