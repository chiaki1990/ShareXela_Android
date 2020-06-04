package ga.sharexela.sharexela

import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File






//CrearArticuloFragmentとEditarArticuloFragmentで使用している
fun retrieveArticuloData(etArticuloTitle: EditText, etArticuloDescription: EditText,
                         spArticuloCategory: Spinner, spSelectPais:Spinner, spSelectDepartamento:Spinner,
                         spSelectMunicipio:Spinner, tvCrearArticuloPoint: TextView, tvCrearArticuloRadius: TextView
)
        :ItemSerializerModel {

    //入力データの取得 //なんでpoontとradiusを追加していないのか理由がわかっていない。。。
    val title           = etArticuloTitle.text.toString()
    val description     = etArticuloDescription.text.toString()
    val category:String = spArticuloCategory.selectedItem.toString()
    val adm0: String    = spSelectPais.selectedItem.toString()
    val adm1: String    = spSelectDepartamento.selectedItem.toString()
    val adm2: String    = spSelectMunicipio.selectedItem.toString()
    val point: String   = tvCrearArticuloPoint.text.toString()
    val radius: Int     = tvCrearArticuloRadius.text.toString().toInt()
    //CategorySerializerModelオブジェクトの生成
    val categoryObj     = CategorySerializerModel(name=category)
    //ItemSerializerModelオブジェクトの作成
    val itemObj = ItemSerializerModel(title=title, description=description, category=categoryObj, adm0=adm0, adm1=adm1, adm2=adm2, point=point, radius=radius )

    return itemObj
}




//CrearArticuloFragmentとEditarArticuloFragmentで使用している
fun makeImgagePartForRetrofit(imageViewFilePath:String?, formName:String): MultipartBody.Part?{
    var part: MultipartBody.Part? = null

    if (imageViewFilePath != ""){

        try {
            var file = File(imageViewFilePath)

            println("FILEの内容チェック")
            println(file)

            var fileBody = RequestBody.create(MediaType.parse("image/*"), file)
            part = MultipartBody.Part.createFormData(formName, file.name, fileBody)
        }catch (e:NullPointerException){
            println(e)
        }
    }

    println("part1の標準出力をじっこう")
    println(part == null)
    println(imageViewFilePath)
    return part
}






fun setAdapterToCategotySpinner(spinner: Spinner){
    val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.categoryList, android.R.layout.simple_list_item_1)
    //spArticuloCategory.adapter = adapter
    spinner.adapter = adapter
}

fun setValueToCategotySpinner(itemObj: ItemSerializerModel, spinner: Spinner) {
    val categoryList: Array<String> = MyApplication.appContext.resources.getStringArray(R.array.categoryList)
    val indexOfCategory = categoryList.indexOf(itemObj.category!!.name)
    //spArticuloCategory.setSelection(indexOfCategory)
    spinner.setSelection(indexOfCategory)
}

// * CrearArticuloFragment, EditarArticuloFragmentの他にProfile編集のEditAreaInfoFragmentでも使用する
fun setAdapterToPaisSpinner(spinner: Spinner){
    // R.id.spSelectPais用の関数
    val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.paisList, android.R.layout.simple_list_item_1)
    spinner.adapter = adapter
}

// * CrearArticuloFragment, EditarArticuloFragmentの他にProfile編集のEditAreaInfoFragmentでも使用する
fun setValueToPaisSpinner(strPais: String, spinner: Spinner) {
    val paisList: Array<String> = MyApplication.appContext.resources.getStringArray(R.array.paisList)
    val indexOfPais = paisList.indexOf(strPais)
    spinner.setSelection(indexOfPais)
}

// * CrearArticuloFragment, EditarArticuloFragmentの他にProfile編集のEditAreaInfoFragmentでも使用する
fun setAdapterToDepartamentoSpinner(spinner: Spinner){
    //R.id.spSelectDepartamento用の関数
    val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.departamentoList, android.R.layout.simple_list_item_1)
    spinner.adapter = adapter
}

// * CrearArticuloFragment, EditarArticuloFragmentの他にProfile編集のEditAreaInfoFragmentでも使用する
fun setValueToDepartamentoSpinner(strDepartamento: String, spinner: Spinner) {
    val departamentoList: Array<String> = MyApplication.appContext.resources.getStringArray(R.array.departamentoList)
    val indexOfDepartamentos = departamentoList.indexOf(strDepartamento)
    spinner.setSelection(indexOfDepartamentos)
}

// * CrearArticuloFragment, EditarArticuloFragmentの他にProfile編集のEditAreaInfoFragmentでも使用する
fun setAdapterToMunicipioSpinner(spinner: Spinner){
    //R.id.spSelectMunicipio用の関数
    val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.municipioList, android.R.layout.simple_list_item_1)
    spinner.adapter = adapter
}

// * CrearArticuloFragment, EditarArticuloFragmentの他にProfile編集のEditAreaInfoFragmentでも使用する
fun setValueToMunicipioSpinner(strMunicipio: String, spinner: Spinner) {
    val municipioList: Array<String> = MyApplication.appContext.resources.getStringArray(R.array.municipioList)
    val indexOfMunicipos = municipioList.indexOf(strMunicipio)
    spinner.setSelection(indexOfMunicipos)
}




