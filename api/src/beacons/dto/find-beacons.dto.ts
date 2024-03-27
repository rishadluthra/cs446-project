import { Transform } from 'class-transformer';
import {
  IsArray,
  IsEnum,
  IsNotEmpty,
  IsNumber,
  IsOptional,
} from 'class-validator';
import { Tag } from '../beacon.schema';

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
  @IsEnum(Tag, { each: true })
  tags: Tag[];
}
