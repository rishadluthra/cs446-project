import {
    BadRequestException,
    Body,
    Controller,
    Get,
    Param,
    Post,
    Query,
    UseGuards,
  } from '@nestjs/common';
import { ReviewsService } from './reviews.service';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { CurrentUser } from 'src/decorators/user.decorator';
import { User } from 'src/users/user.schema';
import { CreateReviewDto } from './dto';
import { Review } from './review.schema';

@Controller('reviews')
export class ReviewsController {

    constructor(private readonly reviewsService: ReviewsService) {}

    @UseGuards(JwtAuthGuard)
    @Post()
    async create(
        @CurrentUser() currentUser: Partial<User>,
        @Body() createReviewInput: CreateReviewDto
    ): Promise<Review> {
        try {
            const review = await this.reviewsService.create(createReviewInput, currentUser.id);
            return review;
        } catch (error) {
            throw new BadRequestException(error.message)
        }
    }

    @UseGuards(JwtAuthGuard)
    @Get()
    async findByTargetEmail(@Query('targetEmail') targetEmail: string): Promise<Review[]> {
        try {
            return this.reviewsService.findByTargetEmail(targetEmail)
        } catch (error) {
            throw new BadRequestException(error.message)
        }
    }

}