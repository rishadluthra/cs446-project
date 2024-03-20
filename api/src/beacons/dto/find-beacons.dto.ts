import { Transform } from 'class-transformer';
import { IsArray, IsNotEmpty, IsNumber, IsOptional } from 'class-validator';

export class FindBeaconsDto {
  @Transform(({ value }) => Number(value))
  @IsNotEmpty()
  @IsNumber()
  latitude: number;

  @Transform(({ value }) => Number(value))
  @IsNotEmpty()
  @IsNumber()
  longitude: number;

  @Transform(({ value }) => Number(value))
  @IsNotEmpty()
  @IsNumber()
  maxDistance: number;

  @IsOptional()
  @IsArray()
  tags: string[];
}
