import { Transform, Type } from 'class-transformer';
import {
  IsNotEmpty,
  IsNumber,
  IsObject,
  IsString,
  ValidateNested,
} from 'class-validator';

class LocationDto {
  @Transform(({ value }) => Number(value))
  @IsNumber()
  latitude: number;

  @Transform(({ value }) => Number(value))
  @IsNumber()
  longitude: number;
}

export class CreateBeaconDto {
  @IsNotEmpty()
  @IsString()
  creatorId: string;

  @IsNotEmpty()
  @IsString()
  title: string;

  @IsNotEmpty()
  @IsString()
  description: string;

  @IsObject()
  @ValidateNested()
  @Type(() => LocationDto)
  location: LocationDto;
}
