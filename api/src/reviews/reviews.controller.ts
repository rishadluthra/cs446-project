import {
  BadRequestException,
  Body,
  Controller,
  Get,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';

import { CreateReviewDto } from './dto';
import { Review } from './review.schema';
import { ReviewsService, ReviewsWithAverageRating } from './reviews.service';

import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../decorators/user.decorator';
import { User } from '../users/user.schema';

@Controller('reviews')
export class ReviewsController {
  constructor(private readonly reviewsService: ReviewsService) {}

  @UseGuards(JwtAuthGuard)
  @Post()
  async create(
    @CurrentUser() currentUser: Partial<User>,
    @Body() createReviewInput: CreateReviewDto,
  ): Promise<Review> {
    try {
      const review = await this.reviewsService.create(
        createReviewInput,
        currentUser.id,
      );
      return review;
    } catch (error) {
      throw new BadRequestException(error.message);
    }
  }

  @UseGuards(JwtAuthGuard)
  @Get()
  async findByTargetEmail(
    @Query('targetEmail') targetEmail: string,
  ): Promise<ReviewsWithAverageRating> {
    return this.reviewsService.findByTargetEmail(targetEmail);
  }
}
