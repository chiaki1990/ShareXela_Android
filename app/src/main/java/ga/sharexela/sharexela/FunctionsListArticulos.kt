package ga.sharexela.sharexela






fun categoryDisplayMaker(strCategoryNumber:String):String{
    //カテゴリーNumberから該当するリストの文字列を返す
    //用途:detail, listのcategory値は数値だからそこで使う。
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
