package cn.vove7.regEngine

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
                Pair("[abc]*", listOf(Pair("aaa", true), Pair("abc", true), Pair("aabaad", false), Pair("aabaada", false))),
                Pair("%123", listOf(Pair("abc123", true))),
                Pair("[0-9]+", listOf(/*Pair("1323", true),*/ Pair("", false), Pair("e12dd", false))),
                Pair("abc?#", listOf(Pair("abcqw", false), Pair("ab12", true), Pair("abc80", true))),
                Pair("(123)?abc", listOf(Pair("12ab", false), Pair("abc", true))),
                Pair("#%", listOf(Pair("12", true), Pair("12ab", true)))
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

    private val title = hashMapOf<Array<*>, String>(
            Pair(customTestList, "普通测试"),
            Pair(paramTestlist, "参数测试"),
            Pair(otherTestlist, "其他测试")
    )


    @Test
    fun test() {
        arrayOf(customTestList, paramTestlist, otherTestlist).forEach {
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
        assertEquals("(1|2|3)+".toParamRegex().match("1") == null, false)
        assertEquals("[123]?abc(abc?)?".toParamRegex().match("2abcab") == null, false)
    }


}