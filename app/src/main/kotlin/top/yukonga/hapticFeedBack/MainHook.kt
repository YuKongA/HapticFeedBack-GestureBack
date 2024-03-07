package top.yukonga.hapticFeedBack

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createBeforeHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXHelper.initHandleLoadPackage(lpparam)
        EzXHelper.setLogTag("HapticFeedBack")
        when (lpparam.packageName) {
            "com.miui.home" -> {
                val hapticFeedbackCompatV2 = loadClass("com.miui.home.launcher.common.HapticFeedbackCompatV2")
                val gestureStubView = loadClass("com.miui.home.recents.GestureStubView")

                hapticFeedbackCompatV2.methodFinder().filterByName("lambda\$performGestureReadyBack\$11\$HapticFeedbackCompatV2").first().createHook {
                    replace {
                        val mHapticHelper = it.thisObject.objectHelper().getObjectOrNull("mHapticHelper")
                        XposedHelpers.callMethod(mHapticHelper, "performExtHapticFeedback", 162)
                    }
                }

                hapticFeedbackCompatV2.methodFinder().filterByName("lambda\$performGestureBackHandUp\$12\$HapticFeedbackCompatV2").first().createHook {
                    replace {
                        val mHapticHelper = it.thisObject.objectHelper().getObjectOrNull("mHapticHelper")
                        XposedHelpers.callMethod(mHapticHelper, "performExtHapticFeedback", 163)
                    }
                }

                gestureStubView.methodFinder().filterByName("injectKeyEvent").filterByParamCount(2).first().createBeforeHook {
                    it.args[1] = true
                }

            }

            else -> return
        }
    }
}