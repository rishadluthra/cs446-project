import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { plainToClass } from 'class-transformer';
import { Strategy } from 'passport-local';

import { AuthService } from './auth.service';
import { LoginDto } from './dto';

import { User } from '../users/user.schema';

@Injectable()
export class LocalStrategy extends PassportStrategy(Strategy) {
  constructor(private readonly authService: AuthService) {
    super({ usernameField: 'email' });
  }

  async validate(email: string, password: string): Promise<User> {
    const loginInput = plainToClass(LoginDto, { email, password });
    const user = await this.authService.validateUser(loginInput);
    if (!user) {
      throw new UnauthorizedException();
    }

    return user;
  }
}
