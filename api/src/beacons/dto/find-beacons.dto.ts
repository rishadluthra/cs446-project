import { Transform } from 'class-transformer';
import { IsNotEmpty, IsNumber } from 'class-validator';

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
}
