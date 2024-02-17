package com.pavellukyanov.themartian.domain.entity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.data.dto.RoverName
import com.pavellukyanov.themartian.utils.DateFormatter

@Entity(tableName = "rover_info")
data class Rover(
    @PrimaryKey
    val roverName: String,
    val landingDate: String,
    val landingDateFormat: String,
    val launchDate: String,
    val status: String,
    val maxDate: String,
    val maxDateFormat: String,
    val totalPhotos: Int,
    val type: RoverName
) {
    @Composable
    fun Content(
        onClick: (Rover) -> Unit
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth()
                .background(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp))
                .clickable { onClick(this) }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                //Header
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    //Rover Name
                    Text(
                        modifier = Modifier
                            .weight(3f),
                        text = roverName,
                        color = Color.White,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                    //Status
                    Text(
                        text = status,
                        color = Color.Green,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.End
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                //Dates
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(3f),
                        text = stringResource(R.string.launch_date),
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = launchDate,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(3f),
                        text = stringResource(R.string.landing_date),
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = landingDateFormat,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                //Photos
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(3f),
                        text = stringResource(R.string.last_photo_date),
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = maxDateFormat,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(3f),
                        text = stringResource(R.string.total_photo),
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = totalPhotos.toString(),
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

fun RoverItemDto.toRover(): Rover = Rover(
    roverName = name,
    landingDate = landingDate,
    landingDateFormat = DateFormatter.format(landingDate),
    launchDate = DateFormatter.format(launchDate),
    status = status,
    maxDate = maxDate,
    maxDateFormat = DateFormatter.format(maxDate),
    totalPhotos = totalPhotos,
    type = RoverName.entries.find { it.roverName == name } ?: RoverName.CURIOSITY
)