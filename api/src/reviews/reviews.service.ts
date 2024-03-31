import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { CreateReviewDto } from './dto';
import { Review, ReviewDocument } from './review.schema';

import { UsersService } from '../users/users.service';

@Injectable()
export class ReviewsService {
  constructor(
    @InjectModel(Review.name)
    private readonly reviewModel: Model<ReviewDocument>,
    private readonly usersService: UsersService,
  ) {}

  async create(
    { targetEmail, ...reviewBody }: CreateReviewDto,
    creatorId: string,
  ): Promise<Review> {
    const target = await this.usersService.findOneByEmail(targetEmail);

    if (!target) {
      throw new Error('Invalid target user');
    }
    if (target.id === creatorId) {
      throw new Error('Self review');
    }

    return this.reviewModel.create({
      targetId: target.id,
      ...reviewBody,
      creatorId,
    });
  }

  async findByTargetEmail(targetEmail: string): Promise<Review[]> {
    const target = await this.usersService.findOneByEmail(targetEmail);

    if (!target) {
      throw new Error('Invalid target user');
    }

    return this.reviewModel.find({ targetId: target.id });
  }
}
