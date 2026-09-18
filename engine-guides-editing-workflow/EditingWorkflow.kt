import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

private const val BRAND_BANNER_NAME = "Brand banner"
private const val COMPANY_NAME = "Company name"
private const val ATTENDEE_NAME = "Attendee name"

suspend fun editingWorkflow(
    engine: Engine,
    restoreEngineState: Boolean = false,
) = withContext(engine.dispatcher) {
    val previousRole = if (restoreEngineState) engine.editor.getRole() else null
    val previousSelectionMode = if (restoreEngineState) {
        engine.editor.getSettingEnum("doubleClickSelectionMode")
    } else {
        null
    }
    val previousGlobalScopes = if (restoreEngineState) {
        engine.editor.findAllScopes().associateWith { scope ->
            engine.editor.getGlobalScope(key = scope)
        }
    } else {
        emptyMap()
    }
    val roleCustomization = customizeEditingWorkflowRoles(engine, this)

    try {
        val template = createEditingWorkflowTemplate(engine)
        engine.block.forceLoadResources(listOf(template.companyName, template.attendeeName))
    } finally {
        withContext(NonCancellable) {
            roleCustomization.cancelAndJoin()
            previousRole?.let(engine.editor::setRole)
            previousSelectionMode?.let {
                engine.editor.setSettingEnum("doubleClickSelectionMode", value = it)
            }
            previousGlobalScopes.forEach { (scope, globalScope) ->
                engine.editor.setGlobalScope(key = scope, globalScope = globalScope)
            }
        }
    }
}

internal fun createEditingWorkflowTemplate(engine: Engine): EditingWorkflowTemplate {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 720F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(background, "Card background")
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 720F)
    engine.block.setHeight(background, value = 1080F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(background, color = Color.fromRGBA(247, 249, 252, 255))
    engine.block.appendChild(parent = page, child = background)

    // highlight-android-templateScene
    val brandBanner = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(brandBanner, BRAND_BANNER_NAME)
    engine.block.setShape(brandBanner, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(brandBanner, value = 640F)
    engine.block.setHeight(brandBanner, value = 220F)
    engine.block.setPositionX(brandBanner, value = 40F)
    engine.block.setPositionY(brandBanner, value = 48F)
    engine.block.setFill(brandBanner, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(brandBanner, color = Color.fromHex("#FF0B1220"))
    engine.block.appendChild(parent = page, child = brandBanner)

    val companyName = engine.block.create(DesignBlockType.Text)
    engine.block.setName(companyName, COMPANY_NAME)
    engine.block.setWidthMode(companyName, mode = SizeMode.AUTO)
    engine.block.setHeightMode(companyName, mode = SizeMode.AUTO)
    engine.block.setPositionX(companyName, value = 88F)
    engine.block.setPositionY(companyName, value = 122F)
    engine.block.replaceText(companyName, text = "IMGLY Labs")
    engine.block.setTextColor(companyName, color = Color.fromHex("#FFFFFFFF"))
    engine.block.appendChild(parent = page, child = companyName)

    val attendeeName = engine.block.create(DesignBlockType.Text)
    engine.block.setName(attendeeName, ATTENDEE_NAME)
    engine.block.setWidthMode(attendeeName, mode = SizeMode.AUTO)
    engine.block.setHeightMode(attendeeName, mode = SizeMode.AUTO)
    engine.block.setPositionX(attendeeName, value = 88F)
    engine.block.setPositionY(attendeeName, value = 404F)
    engine.block.replaceText(attendeeName, text = "Alex Morgan")
    engine.block.setTextColor(attendeeName, color = Color.fromHex("#FF0B1220"))
    engine.block.setBackgroundColor(attendeeName, color = Color.fromRGBA(231, 240, 255, 255))
    engine.block.setBackgroundColorEnabled(attendeeName, enabled = true)
    engine.block.setFloat(attendeeName, property = "backgroundColor/paddingLeft", value = 24F)
    engine.block.setFloat(attendeeName, property = "backgroundColor/paddingTop", value = 20F)
    engine.block.setFloat(attendeeName, property = "backgroundColor/paddingRight", value = 24F)
    engine.block.setFloat(attendeeName, property = "backgroundColor/paddingBottom", value = 20F)
    engine.block.setFloat(attendeeName, property = "backgroundColor/cornerRadius", value = 18F)
    engine.block.appendChild(parent = page, child = attendeeName)
    // highlight-android-templateScene

    // highlight-android-roles
    // Roles define user types: "Creator", "Adopter", "Viewer", "Presenter".
    val role = engine.editor.getRole()
    println("Current role: $role") // "Creator"

    engine.editor.setRole("Adopter")
    val adopterRole = engine.editor.getRole()
    println("Preview role: $adopterRole") // "Adopter"

    engine.editor.setRole("Creator")
    // highlight-android-roles

    // highlight-android-globalScopes
    // Defer to the block-level settings so the template controls the Adopter experience.
    engine.editor.setGlobalScope(key = "editor/select", globalScope = GlobalScope.DEFER)
    engine.editor.setGlobalScope(key = "layer/move", globalScope = GlobalScope.DEFER)
    engine.editor.setGlobalScope(key = "text/edit", globalScope = GlobalScope.DEFER)
    engine.editor.setGlobalScope(key = "text/character", globalScope = GlobalScope.DEFER)
    engine.editor.setGlobalScope(key = "lifecycle/destroy", globalScope = GlobalScope.DEFER)

    val moveScope = engine.editor.getGlobalScope(key = "layer/move")
    val allScopes = engine.editor.findAllScopes()
    println("Global 'layer/move' scope: $moveScope")
    println("Available scopes: ${allScopes.count()}")
    // highlight-android-globalScopes

    // highlight-android-blockScopes
    engine.block.setScopeEnabled(page, key = "editor/select", enabled = false)
    engine.block.setScopeEnabled(page, key = "layer/move", enabled = false)
    engine.block.setScopeEnabled(page, key = "lifecycle/destroy", enabled = false)

    engine.block.setScopeEnabled(background, key = "editor/select", enabled = false)
    engine.block.setScopeEnabled(background, key = "layer/move", enabled = false)
    engine.block.setScopeEnabled(background, key = "lifecycle/destroy", enabled = false)

    // Locked brand elements stay fixed for Adopters.
    engine.block.setScopeEnabled(brandBanner, key = "editor/select", enabled = false)
    engine.block.setScopeEnabled(brandBanner, key = "layer/move", enabled = false)
    engine.block.setScopeEnabled(brandBanner, key = "lifecycle/destroy", enabled = false)

    engine.block.setScopeEnabled(companyName, key = "editor/select", enabled = false)
    engine.block.setScopeEnabled(companyName, key = "layer/move", enabled = false)
    engine.block.setScopeEnabled(companyName, key = "text/edit", enabled = false)
    engine.block.setScopeEnabled(companyName, key = "text/character", enabled = false)
    engine.block.setScopeEnabled(companyName, key = "lifecycle/destroy", enabled = false)

    // Keep the attendee name editable but fixed in place.
    engine.block.setScopeEnabled(attendeeName, key = "editor/select", enabled = true)
    engine.block.setScopeEnabled(attendeeName, key = "layer/move", enabled = false)
    engine.block.setScopeEnabled(attendeeName, key = "text/edit", enabled = true)
    engine.block.setScopeEnabled(attendeeName, key = "text/character", enabled = false)
    engine.block.setScopeEnabled(attendeeName, key = "lifecycle/destroy", enabled = false)
    // highlight-android-blockScopes

    // highlight-android-checkPermissions
    engine.editor.setRole("Creator")

    val creatorCanSelectBrandBanner = engine.block.isAllowedByScope(brandBanner, key = "editor/select")
    val creatorCanEditAttendeeName = engine.block.isAllowedByScope(attendeeName, key = "text/edit")

    println("Creator can select the banner: $creatorCanSelectBrandBanner") // true
    println("Creator can edit the attendee name: $creatorCanEditAttendeeName") // true
    // highlight-android-checkPermissions

    // highlight-android-switchRole
    engine.editor.setRole("Adopter")

    val adopterCanSelectBrandBanner = engine.block.isAllowedByScope(brandBanner, key = "editor/select")
    val adopterCanEditAttendeeName = engine.block.isAllowedByScope(attendeeName, key = "text/edit")

    println("Adopter can select the banner: $adopterCanSelectBrandBanner") // false
    println("Adopter can edit the attendee name: $adopterCanEditAttendeeName") // true

    engine.editor.setRole("Creator")
    // highlight-android-switchRole

    return EditingWorkflowTemplate(
        brandBanner = brandBanner,
        companyName = companyName,
        attendeeName = attendeeName,
    )
}

// highlight-android-customizeRoleBehavior
fun customizeEditingWorkflowRoles(
    engine: Engine,
    scope: CoroutineScope,
): Job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
    engine.editor.onRoleChanged().collect { role ->
        if (role == "Adopter") {
            engine.editor.setGlobalScope(key = "appearance/filter", globalScope = GlobalScope.ALLOW)
            engine.editor.setGlobalScope(key = "appearance/effect", globalScope = GlobalScope.ALLOW)
        }
    }
}
// highlight-android-customizeRoleBehavior

fun findEditingWorkflowTemplate(engine: Engine): EditingWorkflowTemplate = EditingWorkflowTemplate(
    brandBanner = requireNotNull(engine.block.findByName(BRAND_BANNER_NAME).firstOrNull()),
    companyName = requireNotNull(engine.block.findByName(COMPANY_NAME).firstOrNull()),
    attendeeName = requireNotNull(engine.block.findByName(ATTENDEE_NAME).firstOrNull()),
)
