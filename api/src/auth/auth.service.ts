import { Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { compare, hash } from 'bcryptjs';

import { AccessTokenDto, LoginDto } from './dto';

import { EmailService } from '../email/email.service';
import { CreateUserDto } from '../users/dto';
import { User } from '../users/user.schema';
import { UsersService } from '../users/users.service';

@Injectable()
export class AuthService {
  constructor(
    private readonly jwtService: JwtService,
    private readonly emailService: EmailService,
    private readonly usersService: UsersService,
  ) {}

  async validateUser({ email, password }: LoginDto): Promise<User | null> {
    const user = await this.usersService.findOneByEmail(email);
    const valid = user && (await compare(password, user.password));
    if (!valid) {
      return null;
    }

    user.password = undefined;
    return user;
  }

  async login(user: User | Partial<User>): Promise<AccessTokenDto> {
    return {
      access_token: await this.jwtService.signAsync({
        email: user.email,
        sub: user.id,
      }),
    };
  }

  async register({
    firstName,
    lastName,
    email,
    password,
  }: CreateUserDto): Promise<AccessTokenDto> {
    const exists = await this.usersService.findOneByEmail(email);
    if (exists) {
      throw new Error('Email already registered. Please login instead.');
    }

    const user = await this.usersService.create({
      firstName,
      lastName,
      email,
      password: await hash(password, 10),
    });

    return this.login(user);
  }

  async sendVerificationEmail(recipient: string): Promise<string> {
    const randomCode = Math.floor(Math.random() * 1000000)
      .toString()
      .padStart(6, '0');

    const emailBody = {
      subject: 'Beacon Verification Code',
      text: `Here is the verification code for your Beacon Account: ${randomCode}`,
    };

    try {
      await this.emailService.sendEmail(recipient, emailBody);
      return randomCode;
    } catch (error) {
      throw error;
    }
  }
}
