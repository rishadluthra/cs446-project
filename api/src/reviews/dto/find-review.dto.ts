import { IsNotEmpty, IsString, IsMongoId } from "class-validator";
import { Transform } from "class-transformer"; 

export class FindReviewDto {
    @IsNotEmpty()
    @IsString()
    @Transform(({value}) => String(value))
    @IsMongoId()
    targetId: string;
}