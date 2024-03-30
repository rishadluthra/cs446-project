import {
    BadRequestException,
    Body,
    Controller,
    Get,
    Post,
    Query,
    UseGuards,
  } from '@nestjs/common';
import { ReviewsService } from './reviews.service';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { CurrentUser } from 'src/decorators/user.decorator';
import { User } from 'src/users/user.schema';
import { CreateReviewDto, FindReviewDto } from './dto';
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
    async findByTargetId(@Query() findReviewInput: FindReviewDto): Promise<Review[]> {
        try {
            return this.reviewsService.findByTargetId(findReviewInput)
        } catch (error) {
            throw new BadRequestException(error.message)
        }
    }

}