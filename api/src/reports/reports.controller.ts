import {
  BadRequestException,
  Body,
  Controller,
  Post,
  UseGuards,
} from '@nestjs/common';
import { ReportsService } from './reports.service';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { Report } from './report.schema';
import { CurrentUser } from 'src/decorators/user.decorator';
import { User } from 'src/users/user.schema';

@Controller('reports')
export class ReportsController {
  constructor(private readonly reportsService: ReportsService) {}

  @UseGuards(JwtAuthGuard)
  @Post()
  async create(
    @CurrentUser() currentUser: Partial<User>,
    @Body('targetEmail') targetEmail: string,
  ): Promise<Report> {
    try {
      const report = await this.reportsService.create(
        targetEmail,
        currentUser.id,
      );
      return report;
    } catch (error) {
      throw new BadRequestException(error.message);
    }
  }
}
