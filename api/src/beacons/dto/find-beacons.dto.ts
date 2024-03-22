import { Transform } from 'class-transformer';
import { IsEnum, IsNotEmpty, IsNumber, IsOptional } from 'class-validator';
import { Tags } from '../beacon.schema';

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
  @Transform(({ value }) => Tags[value])
  @IsEnum(Tags)
  tag: Tags;
}
