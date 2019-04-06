package cn.vove7.regEngine.test

import cn.vove7.regEngine.toParamRegex
import cn.vove7.regEngine.u.Vog
import org.junit.Test
import kotlin.test.assertEquals

/**
 * # MainTest
 * Created by 11324.
 * Date: 2019/3/17
 */
class MainTest {

    private val customTestList =
        arrayOf(
//                Pair("(123)?", listOf(Pair("", true), Pair("123", true), Pair("1234", false))),
                Pair("123|abc", listOf(Pair("123abc", false), Pair("123", true), Pair("abc", true))),
                Pair("[a-z123]*", listOf(Pair("avz", true), Pair("123", true))),
                Pair("12345", listOf(Pair("12345", true), Pair("abc", false))),
                Pair("[abc]{2,3}", listOf(Pair("aaa", true), Pair("abc", true), Pair("aabaa", false), Pair("adabaada", false))),
                Pair("%123", listOf(Pair("abc123", true))),
                Pair("[0-9]+", listOf(/*Pair("1323", true),*/ Pair("", false), Pair("e12dd", false))),
                Pair("abc?#", listOf(Pair("abcqw", false), Pair("ab12", true), Pair("abc80", true))),
                Pair("(123)?abc", listOf(Pair("12ab", false), Pair("abc", true))),
                Pair("#%", listOf(Pair("12", true), Pair("12ab", true))),
                Pair("123?|45", listOf(Pair("12", true), Pair("123", true), Pair("1245", false)))

        )
    private val paramTestlist = arrayOf(
            Pair("(帮我)?@{text}第@{#index}个", listOf(Pair("点击第二个", true),
                    Pair("帮我点击第x个", false), Pair("帮我点击第34个", true)))
            , Pair("@{text}123%", listOf(Pair("你好123", true), Pair("你好来啦", false)))
    )
    private val otherTestlist = arrayOf(
            Pair("(1|2|3)+", listOf(Pair("11231112121232", true),
                    Pair("12131314231", false), Pair("", false)))

            , Pair("[123]?abc(abc?)?", listOf(Pair("2abcab", true),
            Pair("abcabc", true), Pair("123abc", false))
    )
    )
    private val cmdRegexList = arrayOf(
            Pair("@{city}的?天气", listOf(Pair("北京天气", true), Pair("北京的天气", true), Pair("天气1", false))),
            Pair("打(电话)?给@{name}", listOf(Pair("打给略略略", true), Pair("打电话给123", true))),
            Pair("给@{name}打电话", listOf(Pair("给略略略打电话", true))),
            Pair("@{date}叫我@{content}", listOf(Pair("明天八点叫我起床", true))),
            Pair("定个@{date}的闹钟", listOf(Pair("定个明天八点的闹钟", true))),
            Pair("@{date}(提醒|通知)我@{content}", listOf(Pair("明天提醒我开会", true), Pair("明天开会", false))),
            Pair("(导航到?|带我去)(附近的)?@{place}", listOf(Pair("去玩", false), Pair("带我去附近的厕所", true), Pair("带我去学校", true), Pair("导航到北京", true), Pair("导航北京", true))),
            Pair("[大]+点声", listOf(Pair("大点声", true), Pair("点声", false), Pair("大大大大大点声", true))),
            Pair("(帮我)?(使用|打开|开启|启动)@{msg}", listOf(Pair("打开qq", true), Pair("帮我开启蓝牙", true)))

    )

    private val title = hashMapOf<Array<*>, String>(
            Pair(customTestList, "普通测试"),
            Pair(paramTestlist, "参数测试"),
            Pair(otherTestlist, "其他测试"),
            Pair(cmdRegexList, "指令正则测试")
    )


    @Test
    fun test() {
        arrayOf(customTestList, paramTestlist, otherTestlist, cmdRegexList).forEach {
            Vog.d(" ------------${title[it]}------------")
            it.forEach { item ->
                val regex = item.first.toParamRegex()
                item.second.forEach { iv ->
                    val s = iv.first
                    Vog.d("-----------------------")
                    Vog.d(" 测试 --> " + item.first + "\t" + s)
                    val b = regex.match(s)
                    if (b != null) {
                        Vog.d("匹配成功$b")
                        assertEquals(iv.second, true, item.first + "\t" + s)
                    } else {
                        Vog.e("匹配失败")
                        assertEquals(iv.second, false, item.first + "\t" + s)
                    }
                    Vog.d("-----------------------\n\n")
                }
            }
            Vog.d(" ------------${title[it]} 结束------------\n\n\n")
        }
    }

    @Test
    fun singleTest() {

        println("%123%".toParamRegex().match("123"))// true

        println("@{date}(叫|提醒)我@{message}".toParamRegex().match("明天八点叫我起床"))// false

        if(true) return

        println("导航到?|带我去".toRegex().matches("导航带我去"))// false

        assertEquals("(导航到?|带我去)(附近的)?@{place}".toParamRegex().match("去玩") == null, true)
        assertEquals("@{date}(提醒|通知)我@{content}".toParamRegex().match("明天提醒我开会") == null, false)
        assertEquals("@{city}的?天气".toParamRegex().match("北京天气") == null, false)
        assertEquals("(1|2|3)+".toParamRegex().match("1") == null, false)
        assertEquals("[123]?abc(abc?)?".toParamRegex().match("2abcab") == null, false)
    }


}