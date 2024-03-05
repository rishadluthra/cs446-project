import {
  BadRequestException,
  Body,
  Controller,
  Get,
  Post,
  UseGuards,
} from '@nestjs/common';

import { CurrentUser } from '../decorators/user.decorator';

import { AuthService } from './auth.service';
import { AccessTokenDto } from './dto';
import { JwtAuthGuard } from './jwt-auth.guard';
import { LocalAuthGuard } from './local-auth.guard';

import { CreateUserDto } from '../users/dto';
import { User } from '../users/user.schema';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @UseGuards(LocalAuthGuard)
  @Post('login')
  async login(
    @CurrentUser() currentUser: Partial<User>,
  ): Promise<AccessTokenDto> {
    return this.authService.login(currentUser);
  }

  @Post('register')
  async register(
    @Body() { firstName, lastName, email, password }: CreateUserDto,
  ): Promise<AccessTokenDto> {
    try {
      const accessToken = await this.authService.register({
        firstName,
        lastName,
        email,
        password,
      });
      return accessToken;
    } catch (error) {
      throw new BadRequestException(error.message);
    }
  }

  @UseGuards(JwtAuthGuard)
  @Get('profile')
  getProfile(@CurrentUser() currentUser: Partial<User>) {
    return currentUser;
  }
}
