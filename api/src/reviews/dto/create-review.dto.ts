import { IsMongoId, IsNotEmpty, IsNumber, IsOptional, IsString } from "class-validator";

export class CreateReviewDto {
    @IsNotEmpty()
    @IsString()
    @IsMongoId()
    targetId: string;

    @IsNotEmpty()
    @IsNumber()
    rating: Number;

    @IsOptional()
    @IsString()
    review: String;
}