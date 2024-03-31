import { IsEmail, IsNotEmpty, IsNumber, IsOptional, IsString, Min, Max } from "class-validator";

export class CreateReviewDto {
    @IsNotEmpty()
    @IsString()
    @IsEmail()
    targetEmail: string;

    @IsNotEmpty()
    @IsNumber()
    @Min(1)
    @Max(5)
    rating: Number;

    @IsOptional()
    @IsString()
    review: String;
}