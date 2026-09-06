package com.tracklab400.app.ui.exercises

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.tracklab400.app.R
import com.tracklab400.app.data.exercises.CoolDownCatalog
import com.tracklab400.app.data.exercises.PoseKind
import com.tracklab400.app.data.exercises.StrengthCatalog
import com.tracklab400.app.data.exercises.WarmUpCatalog

@DrawableRes
fun poseDrawableRes(pose: PoseKind): Int = when (pose) {
    PoseKind.STAND -> R.drawable.pose_stand
    PoseKind.RUNNING -> R.drawable.pose_running
    PoseKind.HEEL_RAISE -> R.drawable.pose_heel_raise
    PoseKind.KNEE_RAISE -> R.drawable.pose_knee_raise
    PoseKind.HAMSTRING_PUMP -> R.drawable.pose_hamstring_pump
    PoseKind.LEG_SWING -> R.drawable.pose_leg_swing
    PoseKind.LATERAL_LUNGE -> R.drawable.pose_lateral_lunge
    PoseKind.HINGE -> R.drawable.pose_hinge
    PoseKind.SKIP -> R.drawable.pose_skip
    PoseKind.BRIDGE_DOWN -> R.drawable.pose_bridge_down
    PoseKind.BRIDGE_UP -> R.drawable.pose_bridge
    PoseKind.SQUAT -> R.drawable.pose_squat
    PoseKind.SPLIT_SQUAT_UP -> R.drawable.pose_splitsquat_up
    PoseKind.SPLIT_SQUAT_DOWN -> R.drawable.pose_splitsquat_down
    PoseKind.NORDIC_START -> R.drawable.pose_nordic_start
    PoseKind.NORDIC_LEAN -> R.drawable.pose_nordic_lean
    PoseKind.PULLUP -> R.drawable.pose_pullup
    PoseKind.PULLUP_UP -> R.drawable.pose_pullup_up
    PoseKind.PROWLER -> R.drawable.pose_prowler
    PoseKind.BOX_LOAD -> R.drawable.pose_box_load
    PoseKind.STEP_UP -> R.drawable.pose_stepup
    PoseKind.PLANK -> R.drawable.pose_plank
    PoseKind.PLANK_KNEE -> R.drawable.pose_plank_knee
    PoseKind.PUSHUP -> R.drawable.pose_pushup
    PoseKind.PUSHUP_LOW -> R.drawable.pose_pushup_low
    PoseKind.PIKE -> R.drawable.pose_pike
    PoseKind.QUAD -> R.drawable.pose_quad
    PoseKind.CHILD -> R.drawable.pose_child
    PoseKind.ITBAND -> R.drawable.pose_itband
    PoseKind.CALF -> R.drawable.pose_calf
    PoseKind.PIGEON -> R.drawable.pose_pigeon
    PoseKind.BREATH -> R.drawable.pose_breath
}

fun poseContentDescription(pose: PoseKind): String = when (pose) {
    PoseKind.STAND -> "Ayakta dik duruş"
    PoseKind.RUNNING -> "Koşu pozisyonu"
    PoseKind.HEEL_RAISE -> "Ayak ucunda yükseliş"
    PoseKind.KNEE_RAISE -> "Yüksek diz kaldırma"
    PoseKind.HAMSTRING_PUMP -> "Öne eğilerek diz çekme"
    PoseKind.LEG_SWING -> "Tek bacak salınımı"
    PoseKind.LATERAL_LUNGE -> "Yana hamle pozisyonu"
    PoseKind.HINGE -> "Kalça mentesi pozisyonu"
    PoseKind.SKIP -> "Yüksek dizli skip adımı"
    PoseKind.BRIDGE_DOWN -> "Yerde sırt üstü başlangıç"
    PoseKind.BRIDGE_UP -> "Kalça kaldırılmış köprü"
    PoseKind.SQUAT -> "Derin çömelme pozisyonu"
    PoseKind.SPLIT_SQUAT_UP -> "Split squat başlangıcı"
    PoseKind.SPLIT_SQUAT_DOWN -> "Split squat alçalmış pozisyon"
    PoseKind.NORDIC_START -> "Nordic başlangıç pozisyonu"
    PoseKind.NORDIC_LEAN -> "Nordic gövde eğimi"
    PoseKind.PULLUP -> "Bar çubuğuna asılma pozisyonu"
    PoseKind.PULLUP_UP -> "Çene bar üstünde pozisyon"
    PoseKind.PROWLER -> "İtme kollu gövde eğimi"
    PoseKind.BOX_LOAD -> "Zıplama hazırlık çömelmesi"
    PoseKind.STEP_UP -> "Tek ayak kutuda adım"
    PoseKind.PLANK -> "Dirsek plank pozisyonu"
    PoseKind.PLANK_KNEE -> "Plankta diz çekme"
    PoseKind.PUSHUP -> "Şınav üst pozisyonu"
    PoseKind.PUSHUP_LOW -> "Şınav alçalmış pozisyon"
    PoseKind.PIKE -> "Oturarak öne uzanma"
    PoseKind.QUAD -> "Ayakta diz çekme esnemesi"
    PoseKind.CHILD -> "Çocuk duruşu (sırt esnemesi)"
    PoseKind.ITBAND -> "Yana eğilmiş esneme duruşu"
    PoseKind.CALF -> "Duvara dayalı baldır esnemesi"
    PoseKind.PIGEON -> "Güvercin duruşu (kalça esnemesi)"
    PoseKind.BREATH -> "Oturur rahat nefes pozisyonu"
}

/** Tek pozisyon çizimi. Yerel VectorDrawable'dan okunur, dış bağlantı yoktur. */
@Composable
fun PoseFigure(
    pose: PoseKind,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = poseDrawableRes(pose)),
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        contentScale = ContentScale.Fit,
    )
}

/**
 * Katalog kimliğinden kütüphane satırı için küçük başlangıç pozisyonu döner.
 * Parantez içindeki soğuma eşlemeleri UI-katı çizimde kullanılır.
 */
fun libraryThumbnail(id: String): PoseKind? = when {
    id.startsWith("warmup_") -> WarmUpCatalog.findById(id)?.poseStart
    id.startsWith("strength_") -> StrengthCatalog.findById(id)?.startPose
    id.startsWith("cooldown_") -> cooldownThumbnail(id)
    else -> null
}

fun cooldownThumbnail(id: String): PoseKind? = when (id) {
    "cooldown_jogging" -> PoseKind.RUNNING
    "cooldown_hamstring" -> PoseKind.PIKE
    "cooldown_quad" -> PoseKind.QUAD
    "cooldown_full_body" -> PoseKind.CHILD
    "cooldown_itband" -> PoseKind.ITBAND
    "cooldown_calf" -> PoseKind.CALF
    "cooldown_glute" -> PoseKind.PIGEON
    "cooldown_breathing" -> PoseKind.BREATH
    else -> null
}