import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Review } from './review.schema';
import { CreateReviewDto, FindReviewDto } from './dto';
import { UsersService } from 'src/users/users.service';

@Injectable()
export class ReviewsService {
    constructor(
        @InjectModel(Review.name)
        private readonly reviewModel: Model<Review>,
        private readonly usersService: UsersService
    ) {}
    async create(
        { targetId, ...reviewBody }: CreateReviewDto,
        creatorId: string
    ): Promise<Review> {
        if ((await this.usersService.findById(targetId)) == null) {
            throw new Error('Invalid target user')
        }
        if (targetId == creatorId) {
            throw new Error('Self review')
        }
        return this.reviewModel.create({
            targetId,
            ...reviewBody,
            creatorId
        });
    }
    async findByTargetId({ targetId }: FindReviewDto): Promise<Review[]> {
        if (this.usersService.findById(targetId) == null) {
            throw new Error('Invalid target user')
        }
        return this.reviewModel.find(
            { targetId }
        )
    }
}