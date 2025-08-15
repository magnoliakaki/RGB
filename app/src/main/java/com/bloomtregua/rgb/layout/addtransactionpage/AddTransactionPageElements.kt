package com.bloomtregua.rgb.layout.addtransactionpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun TransactionInsertBox(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (insertBox, amount, date) = createRefs()

        Box(
            Modifier
                .clip(RoundedCornerShape(15.0.dp))
                .size(350.0.dp, 46.0.dp)
                .background(Color(0.18f, 0.31f, 0.36f, 0.46f))
                .constrainAs(insertBox) {
                    centerTo(parent)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }){}
    }
}


//Box(
//Modifier
//.clip(RoundedCornerShape(15.0.dp))
//.background(Color(0.18f, 0.31f, 0.36f, 0.46f))
//.constrainAs(RectangleConto) {
//    centerTo(parent)
//    width = Dimension.fillToConstraints
//    height = Dimension.fillToConstraints
//}){}