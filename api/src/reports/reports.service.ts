import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { Report, ReportDocument } from './report.schema';

import { UsersService } from '../users/users.service';

@Injectable()
export class ReportsService {
  constructor(
    @InjectModel(Report.name)
    private readonly reportModel: Model<ReportDocument>,
    private readonly usersService: UsersService,
  ) {}

  async create(targetEmail: string, creatorId: string): Promise<Report> {
    const target = await this.usersService.findOneByEmail(targetEmail);

    if (!target) {
      return;
    }

    if (target.id === creatorId) {
      throw new Error('Self report');
    }

    return this.reportModel.create({
      targetId: target.id,
      creatorId,
    });
  }
}
