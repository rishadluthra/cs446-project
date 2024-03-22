import { IsEnum, IsNotEmpty, IsString, Matches } from 'class-validator';
import { Tags } from '../beacon.schema';

const canadianPostalCodePattern =
  /^[ABCEGHJ-NPRSTVXY]\d[ABCEGHJ-NPRSTV-Z][ -]?\d[ABCEGHJ-NPRSTV-Z]\d$/i;

export class CreateBeaconDto {
  @IsNotEmpty()
  @IsString()
  title: string;

  @IsNotEmpty()
  @IsString()
  description: string;

  @IsString()
  @Matches(canadianPostalCodePattern, {
    message: 'Invalid Canadian postal code format',
  })
  postalCode: string;

  @IsNotEmpty()
  @IsEnum(Tags)
  tag: Tags;
}
