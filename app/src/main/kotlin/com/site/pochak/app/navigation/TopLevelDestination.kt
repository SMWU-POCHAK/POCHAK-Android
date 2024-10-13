package com.site.pochak.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.feature.alarm.navigation.AlarmRoute
import com.site.pochak.app.feature.camera.navigation.CameraRoute
import com.site.pochak.app.feature.camera.navigation.PostRoute
import com.site.pochak.app.feature.home.navigation.HomeRoute
import com.site.pochak.app.feature.profile.navigation.ProfileRoute
import kotlin.reflect.KClass
import com.site.pochak.app.feature.home.R as homeR
import com.site.pochak.app.feature.post.R as postR
import com.site.pochak.app.feature.camera.R as cameraR
import com.site.pochak.app.feature.alarm.R as alarmR
import com.site.pochak.app.feature.profile.R as profileR

/**
 * Bottom navigation destinations.
 *
 * @property selectedIconId Icon resource id(selected state).
 * @property unselectedIconId Icon resource id(unselected state).
 * Icon 관리는 :core:ui의 drawable과 PochakIcons.kt에서 관리한다.
 * @property titleTextId Title text resource id.
 * Title text 관리는 각각의 :feature 모듈의 strings.xml에서 관리한다.
 * @property route Route class.
 * Route 관리는 각각의 :feature 모듈의 navigation 패키지에서 관리한다.
 */
enum class TopLevelDestination(
    @DrawableRes val selectedIconId: Int,
    @DrawableRes val unselectedIconId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
) {
    HOME(
        selectedIconId = PochakIcons.HomeFilled,
        unselectedIconId = PochakIcons.Home,
        titleTextId = homeR.string.feature_home_title,
        route = HomeRoute::class
    ),
    POST(
        selectedIconId = PochakIcons.PostFilled,
        unselectedIconId = PochakIcons.Post,
        titleTextId = postR.string.feature_post_title,
        route = PostRoute::class
    ),
    CAMERA(
        selectedIconId = PochakIcons.CameraFilled,
        unselectedIconId = PochakIcons.Camera,
        titleTextId = cameraR.string.feature_camera_title,
        route = CameraRoute::class
    ),
    ALARM(
        selectedIconId = PochakIcons.AlarmFilled,
        unselectedIconId = PochakIcons.Alarm,
        titleTextId = alarmR.string.feature_alarm_title,
        route = AlarmRoute::class
    ),
    PROFILE(
        selectedIconId = PochakIcons.ProfileFilled,
        unselectedIconId = PochakIcons.Profile,
        titleTextId = profileR.string.feature_profile_title,
        route = ProfileRoute::class
    ),
}
