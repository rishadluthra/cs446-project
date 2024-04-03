import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';

import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../decorators/user.decorator';
import { User } from '../users/user.schema';
import { UsersService } from './users.service';

@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @UseGuards(JwtAuthGuard)
  @Get("my_email")
  async getUser(
    @CurrentUser() currentUser: Partial<User>,
  ): Promise<string> {
    const user = await this.usersService.findOneByEmail(currentUser.email);
    return user.email;
  }

  @UseGuards(JwtAuthGuard)
    @Get()
    async isValidUser(
      @CurrentUser() currentUser: Partial<User>,
      @Query('targetEmail') targetEmail: string,
    ): Promise<Boolean> {
      const user = await this.usersService.findOneByEmail(targetEmail);
      if (user == null) return false;
      return true;
    }
}