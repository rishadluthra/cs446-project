import { Controller, Get, Query, UseGuards } from '@nestjs/common';

import { User } from './user.schema';
import { UsersService } from './users.service';

import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../decorators/user.decorator';

@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @UseGuards(JwtAuthGuard)
  @Get('my_email')
  async getUser(@CurrentUser() currentUser: Partial<User>): Promise<string> {
    const user = await this.usersService.findOneByEmail(currentUser.email);
    return user.email;
  }

  @UseGuards(JwtAuthGuard)
  @Get()
  async isValidUser(
    @Query('targetEmail') targetEmail: string,
  ): Promise<boolean> {
    return !!this.usersService.findOneByEmail(targetEmail);
  }
}
