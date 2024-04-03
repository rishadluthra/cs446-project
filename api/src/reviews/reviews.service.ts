import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { CreateReviewDto } from './dto';
import { Review, ReviewDocument } from './review.schema';

import { UsersService } from '../users/users.service';

export type ReviewsWithAverageRating = {
  reviews: Review[];
  averageRating: number;
};

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
      return;
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

  async findByTargetEmail(
    targetEmail: string,
  ): Promise<ReviewsWithAverageRating> {
    const target = await this.usersService.findOneByEmail(targetEmail);

    if (!target) {
      return;
    }

    const reviews = await this.reviewModel.find({ targetId: target.id });

    const sumOfRatings = reviews.reduce(
      (sum, review) => sum + review.rating,
      0,
    );

    let averageRating = 0;
    if (reviews.length !== 0) {
      averageRating = sumOfRatings / reviews.length;
    }

    return {
      reviews,
      averageRating: parseFloat(averageRating.toFixed(2)),
    };
  }
}
