import { Model } from 'mongoose';
import { ReportDocument, Report } from './report.schema';

import { UsersService } from '../users/users.service';
import { InjectModel } from '@nestjs/mongoose';

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
      throw new Error('Self review');
    }
    return this.reportModel.create({
      targetId: target.id,
      creatorId,
    });
  }
}
