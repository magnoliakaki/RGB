package com.bloomtregua.rgb.layout.addtransactionpage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomtregua.rgb.layout.calculator.CalculatorScreen
import com.bloomtregua.rgb.ui.theme.PageTopBottomMargins
import com.bloomtregua.rgb.ui.theme.PercentageForPageMid
import com.bloomtregua.rgb.ui.theme.PercentageToPageWidth
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.viewmodels.TransactionsViewModel

@Composable()
@Preview(showBackground = true)
fun AddTrasactionPagePreview() {
    RGBTheme(dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            AddTransactionPage(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable()
fun AddTransactionPage(
    modifier: Modifier = Modifier,
    transactionsViewModel: TransactionsViewModel = hiltViewModel()
) {
    ConstraintLayout(modifier = modifier) {
        val (calculatorRef, transactionInsertBoxRef) = createRefs()

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


        CalculatorScreen(
            modifier = Modifier
                .constrainAs(calculatorRef)
                {
                top.linkTo(horizontalTopLGuideLine)
                start.linkTo(verticalLeftGuideline)
                end.linkTo(verticalRightGuideline)
                bottom.linkTo(transactionInsertBoxRef.top)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        )

        TransactionInsertBox(modifier = Modifier.constrainAs(transactionInsertBoxRef) {
                start.linkTo(verticalLeftGuideline)
                end.linkTo(verticalRightGuideline)
                top.linkTo(calculatorRef.bottom)
                bottom.linkTo(horizontalBottomGuideLine)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        )
    }
}

