package ga.sharexela.sharexela

import android.widget.Button






fun categoryIdmaker(strCategory: String):String{
    val categoryIndex: Int = MyApplication.appContext.resources.getStringArray(R.array.categoryList).indexOf(strCategory)
    return (categoryIndex + 1 ).toString()
}



//buttonのidからカテゴリーIDを出力する
fun getCategoryNumber(button: Button):String{
    val rsName = MyApplication.appContext.getResources().getResourceEntryName(button.id);
    val categoryNumber = rsName.removePrefix("btn")
    return categoryNumber
}


//カテゴリーIDからカテゴリーのディスプレイを表示する
fun categoryDisplayMaker(strCategoryNumber:String):String{
    //カテゴリーNumberから該当するリストの文字列を返す
    //用途:detail, listのcategory値は数値だからそこで使う。またfavoriteやmyListでも使用している。
    // 逆に文字列から数値を送らなければならないCrear, EditarではcategoryIdmakerを使うことにする。
    val categoryList = MyApplication.appContext.resources.getStringArray(R.array.categoryListForCategoryNumber)
    var categoryMap: MutableMap<Int, String> = mutableMapOf()
    for (category in categoryList){
        val categoryNumber  = category.split(":")[0].toInt()
        val categoryDisplay = category.split(":")[1]
        categoryMap[categoryNumber] = categoryDisplay
    }
    val display:String = categoryMap[strCategoryNumber.toInt()]!!

    return display
}


//
fun getCategoryDisplayByButton(button: Button):String{

    val categoryNumber = getCategoryNumber(button)
    val categoryDisplay = categoryDisplayMaker(categoryNumber)
    return  categoryDisplay
}
