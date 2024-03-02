import { IsNotEmpty, IsString } from 'class-validator';

export class FindMyBeaconsDto {
  @IsNotEmpty()
  @IsString()
  creatorId: string;
}
