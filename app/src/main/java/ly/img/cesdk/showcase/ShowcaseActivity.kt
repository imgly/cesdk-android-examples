package ly.img.cesdk.showcase

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ly.img.cesdk.apparel.ApparelUi
import ly.img.cesdk.core.theme.ShowcaseTheme

class ShowcaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShowcaseTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Showcases.routeScheme) {
                    composable(route = Screen.Showcases.routeScheme) {
                        Showcases(navigateTo = {
                            navController.navigate(it)
                        })
                    }
                    composable(
                        route = Screen.ApparelUi.routeScheme
                    ) {
                        val scene = requireNotNull(it.arguments?.getString("0"))
                        val sceneUri = Uri.parse("file:///android_asset/scenes/$scene.scene")
                        ApparelUi(
                            sceneUri = sceneUri,
                            goBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

sealed class Screen(val routeScheme: String) {

    fun getRoute(vararg args: Any): String {
        return args.foldIndexed(routeScheme) { index, acc, arg ->
            acc.replace("{$index}", arg.toString())
        }
    }
    object Showcases : Screen("showcases")
    object ApparelUi : Screen("apparel-ui?scene={0}")
}
