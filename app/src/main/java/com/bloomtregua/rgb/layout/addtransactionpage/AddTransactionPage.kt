package com.bloomtregua.rgb.layout.addtransactionpage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomtregua.rgb.layout.calculator.CalculatorScreen
import com.bloomtregua.rgb.ui.theme.*
import com.bloomtregua.rgb.viewmodels.TransactionsViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable()
fun AddTransactionPage(
    modifier: Modifier = Modifier,
    transactionsViewModel: TransactionsViewModel = hiltViewModel()
) {
    var transactionDate by remember { mutableStateOf(LocalDate.now()) }

    ConstraintLayout(modifier = modifier) {
        val (calculatorRef, transactionDescriptionRef, transactionDateRef, transactionTimeRef) = createRefs()

        // LINEE GUIDA VERTICALI
        // margine da sinistra
        val verticalLeftGuideline = createGuidelineFromStart(PercentageToPageWidth)
        // margine da destra
        val verticalRightGuideline = createGuidelineFromEnd(PercentageToPageWidth)
        // centro del composable rispetto a sx/dx
        val verticalCenterGuideLine = createGuidelineFromStart(PercentageForPageMid)

        // LINEE GUIDA ORIZZONTALI
        // margine dall'alto del composable
        val horizontalTopLGuideLine = createGuidelineFromTop(PageTopBottomMargins)
        // margine dal basso del composable
        val horizontalBottomGuideLine = createGuidelineFromBottom(PageTopBottomMargins)
        // centro del composable rispetto a top/bottom
        val horizontalCenterGuideLine = createGuidelineFromTop(PercentageForPageMid)

        CalculatorScreen(modifier = Modifier
            .constrainAs(calculatorRef)
            {
            start.linkTo(verticalLeftGuideline)
            end.linkTo(verticalRightGuideline)
            top.linkTo(horizontalTopLGuideLine)
            bottom.linkTo(transactionDescriptionRef.top)
            height = Dimension.wrapContent
            width = Dimension.fillToConstraints
        }, userPreferencesRepository = transactionsViewModel.userPreferencesRepository)

        TransactionDescriptionInputField(modifier = Modifier.constrainAs(transactionDescriptionRef) {
            start.linkTo(verticalLeftGuideline)
            end.linkTo(verticalRightGuideline)
            top.linkTo(calculatorRef.bottom)
            bottom.linkTo(transactionDateRef.top)
            height = Dimension.percent(BarHeightM)
            width = Dimension.fillToConstraints
        })

        TransactionDateInputField(
            currentDate = transactionDate,
            onDateSelected = { newDate ->
                transactionDate = newDate
                // Fai qualcosa con la nuova data
            },
            modifier = Modifier.constrainAs(transactionDateRef) {
            start.linkTo(verticalLeftGuideline)
            end.linkTo(transactionTimeRef.start, MarginXS)
            top.linkTo(transactionDescriptionRef.bottom, MarginXS)
            height = Dimension.percent(BarHeightM)
            width = Dimension.fillToConstraints
        })

        TransactionTimeInputField(
            initialTime = LocalTime.now(),
            onTimeSelected = { newTime ->
                // Fai qualcosa con l'ora selezionata
            },
            modifier = Modifier.constrainAs(transactionTimeRef) {
            start.linkTo(transactionDateRef.end)
            end.linkTo(verticalRightGuideline)
            top.linkTo(transactionDescriptionRef.bottom, MarginXS)
            height = Dimension.percent(BarHeightM)
            width = Dimension.fillToConstraints
        })
    }
}


