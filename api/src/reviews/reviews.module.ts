import { Module } from "@nestjs/common";
import { MongooseModule } from "@nestjs/mongoose";
import { Review, ReviewSchema } from "./review.schema";
import { UsersModule } from "src/users/users.module";
import { ReviewsController } from "./reviews.controller";
import { ReviewsService } from "./reviews.service";

@Module ({
    imports: [
        MongooseModule.forFeature([{ name: Review.name, schema: ReviewSchema}]),
        UsersModule,
    ],
    controllers: [ReviewsController],
    providers: [ReviewsService]
})
export class ReviewsModule {}