package com.example.beacon

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ReviewScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
    // General
    val themeStrategy by viewModel.themeStrategy
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(true) {
        viewModel.refreshOurReviews()
        viewModel.refreshOurEmail()
    }


    // Search Bar Values
    val query = remember { mutableStateOf("") }
    val (searchedUser, setSearchedUser) = remember { mutableStateOf("") }

    // Search Bar Results
    val (displayResults, setDisplayResults) = remember { mutableStateOf(false) }

    // Review
    var showReviewPanel by remember { mutableStateOf(false) }
    val (reviewText, setReviewText) = remember { mutableStateOf("") }
    val (rating, setRating) = remember { mutableIntStateOf(5) }

    // Report
    val (showReportConfirmation, setShowReportConfirmation) = remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(themeStrategy.primaryColor)
    ) {
        LazyColumn(modifier = Modifier.height(750.dp).fillMaxWidth(), verticalArrangement = Arrangement.Top) {
            item{
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Reviews  ",
                                fontSize = 32.sp,
                                color = themeStrategy.primaryTextColor,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = themeStrategy.primaryColor)
                )
            }

            if (showReviewPanel) {
                item {
                    Dialog(
                        onDismissRequest = { showReviewPanel = false }
                    ) {
                        Surface(
                            modifier = Modifier.width(300.dp),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TextField(
                                    value = reviewText,
                                    onValueChange = { setReviewText(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    label = { Text("Enter Review") }
                                )

                                Slider(
                                    value = rating.toFloat(),
                                    onValueChange = { setRating(it.toInt()) },
                                    valueRange = 1f..5f,
                                    steps = 4,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            showReviewPanel = false
                                            viewModel.reviewUser(searchedUser, rating, reviewText)
//                                            Log.d("AAAA", "Before")
                                            viewModel.refreshSearchedReviews(searchedUser)
//                                            Log.d("AAAA", "After")
                                        }
                                    ) {
                                        Text("Submit")
                                    }
                                    Button(
                                        onClick = { showReviewPanel = false }
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (showReportConfirmation) {
                item {
                    AlertDialog(
                        onDismissRequest = { setShowReportConfirmation(false) },
                        title = { Text("Success") },
                        text = {
                            Text(
                                text = "Report Submitted",
                                fontSize = 16.sp
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    setShowReportConfirmation(false)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = themeStrategy.primaryColor,
                                    contentColor = themeStrategy.secondaryColor
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            ) {
                                Text("Close")
                            }
                        }
                    )
                }
            }

//          THIS IS THE SEARCH BAR (FIXED, SHOW APPEAR NO MATTER WHAT)
            item {
                SearchBar(
                    viewModel = viewModel,
                    queryState = query,
                    buttonClick = {
                        setSearchedUser(query.value)
                        Log.d("SEARCHBAR", "Searched User: $searchedUser OurEmail: ${uiState.ourEmail} Query: ${query.value}")
                        if (query.value == "" || searchedUser == uiState.ourEmail || query.value == uiState.ourEmail) {
                            setDisplayResults(false)
                            viewModel.refreshSearchedReviews(uiState.ourEmail)
                        } else {
                            setDisplayResults(true)
                            viewModel.refreshSearchedReviews(query.value)
                        }
                        keyboardController?.hide()
                    },
                    modifier = modifier)
            }

//          IF THE SEARCH STRING DOES NOT HAVE AN USER ASSOCIATED WITH IT
            if (!uiState.validSearch) {
                item {
                    Text(
                        text = "There is no user associated with that email. Please try again.",
                        color = themeStrategy.primaryTextColor,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }
//          IF THE SEARCH STRING DOES HAVE AN USER ASSOCIATED WITH IT
            if (uiState.validSearch && displayResults) {
//              DISPLAY BUTTONS
                item {
                    Column( modifier = modifier.fillMaxWidth()) {
                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Button(
                                onClick = {
                                    showReviewPanel = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(vertical = 0.dp)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = themeStrategy.secondaryColor,
                                    contentColor = themeStrategy.secondaryTextColor
                                )
                            ) {
                                Text("Write a Review")
                            }
                            Button(
                                onClick = {
                                    setShowReportConfirmation(true)
                                    viewModel.reportUser(searchedUser)
                                },
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(vertical = 0.dp)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = themeStrategy.secondaryColor,
                                    contentColor = themeStrategy.secondaryTextColor
                                )
                            ) {
                                Text("Report")
                            }
                        }
                        Text(
                            text = "$searchedUser's Reviews",
                            color = themeStrategy.primaryTextColor,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(horizontal = 16.dp, vertical =  16.dp)
                                .align(alignment = Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "Average Rating: ${uiState.searchOverallRating}",
                            color = themeStrategy.primaryTextColor,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(horizontal = 16.dp, vertical =  16.dp)
                                .align(alignment = Alignment.CenterHorizontally)
                        )
                    }
                }

//              Display No Reviews If User Has No Reviews
                if (uiState.searchedReviews.isEmpty()) {
                    item {
                        Text(
                            text = "There are no reviews for $searchedUser",
                            color = themeStrategy.primaryTextColor,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(horizontal = 16.dp, vertical =  16.dp)
                        )
                    }
                } else {
//              Display Reviews If User has Reviews
                    items(uiState.searchedReviews.size) { i ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = themeStrategy.secondaryColor
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(all = 16.dp)
                            ) {
                                Text(
                                    text = "Comment: ${uiState.searchedReviews[i].review}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    color = themeStrategy.secondaryTextColor
                                )
                                Text(
                                    text = "Rating: ${uiState.searchedReviews[i].rating}/5",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = themeStrategy.secondaryTextColor
                                )
                            }
                        }
                    }
                }
            }

//          SHOW THE USER HIS OWN REVIEWS
            if (uiState.validSearch && !displayResults) {
//              Display No Reviews If You Do Not Have Any
                if (uiState.ourReviews.isEmpty()) {
                    item {
                        Text(
                            text = "You have no reviews.",
                            color = themeStrategy.primaryTextColor,
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "Your Reviews",
                            color = themeStrategy.primaryTextColor,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        )
                        Text(
                            text = "Average Rating: ${uiState.searchOverallRating}",
                            color = themeStrategy.primaryTextColor,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(horizontal = 16.dp, vertical =  16.dp)
                        )
                    }
                    items(uiState.ourReviews.size) { i ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = themeStrategy.secondaryColor
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(all = 16.dp)
                            ) {
                                Text(
                                    text = "Comment: ${uiState.ourReviews[i].review}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    color = themeStrategy.secondaryTextColor
                                )
                                Text(
                                    text = "Rating: ${uiState.ourReviews[i].rating}/5",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = themeStrategy.secondaryTextColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(viewModel: BeaconViewModel, queryState: MutableState<String>, buttonClick: () -> Unit, modifier: Modifier,) {
    val themeStrategy by viewModel.themeStrategy
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextFieldWithLabel(
            viewModel,
            label = "Enter User Email",
            state = queryState,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(end = 4.dp)
                .height(IntrinsicSize.Max)
        )
        Button(
            onClick = { buttonClick() },
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp)
                .height(IntrinsicSize.Max)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeStrategy.primaryColor,
                contentColor = themeStrategy.primaryTextColor
            )
        ) {
            Text("Search")
        }
    }
}
