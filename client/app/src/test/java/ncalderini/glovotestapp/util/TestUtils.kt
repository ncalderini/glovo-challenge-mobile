package ncalderini.glovotestapp.util

import ncalderini.glovotestapp.model.City
import ncalderini.glovotestapp.model.Country

object TestUtils {

    private val city = City("BUE",
        "Buenos Aires",
        "AR",
        "ARS",
        true,
        false,
        "America/Argentina/Buenos_Aires",
        "es",
        listOf(
            "vbcrErb|cJwaCho@elDarEhy@wlBlRrVnvArlCpfAfQ",
            "rnbrEjh{cJpUtW}Fp@",
            "~qcrExdhcJcKmIG_AfNsDlRcHZfLcNdE",
            "nsdrExytcJtHdA`Nyg@?a^{GeA",
            "hc`rE|kncJ`AFyIfQyL|OqB{]hL_C",
            "v~_rEz|`dJ{p@s|AhOcOlkAoU`{@gUiD|DzC}D_Ndu@{k@xm@",
            "jrerEbnicJs_@fBaFwdAwf@xJnQ~MyGdPeE?wdA`eC}N_Ngg@p`AzZiBun@pbAqIhC_GbPoYtcApB`]cs@iCv{A~qCtv@`Ofj@al@xOpFfLka@nfAscAfL{h@ZwdA`l@eBla@yYoJ{oC}{@nF",
            "`jerEbwqcJ|h@s@fLuP|YePL}Eg_@|LkG~Mah@jD",
            "fgzqExf_dJmb@stAgb@grA`C?",
            "bejrE~hpcJiHq_BqM{w@aFstCpMs]_l@cm@cyCbm@x@zRz]oFpBxRsIfBjGjuAle@kDmCsxAtr@cA[l}AvSt~Eu[rOeIzRsg@uAi@d}@mRnj@q~@b|@~s@tm@ff@tInYja@nl@qU~QyvCb\\gCaJ}p@la@mEuHm`A",
            "njdrElfxcJiBtHmNgJma@yd@rEqF",
            "bsdrE`|icJuAkgA}UrHaJpCyIvR",
            "t`arEt~lcJcDaHhc@_eA`e@gaAzDhG",
            "zh`rEz~xcJzzAhcBfD?|i@zQtFeJo[qEZ}a@Z_U{VyCgLjKsc@mb@eA}L{i@`@hDnF",
            "nh`rE|hadJ",
            "hserE~kicJ?xtAbz@kDfLduCg[dPiSjSg[`@eTvaBs~@l`Anp@re@}F~Mym@mq@kKh`@aNwI_l@lq@yu@eP",
            "pb`rE|lncJmPsMrFuNrIeLzOkPfL~HiEnMw@nDeFvG",
            "~uyqElzvcJ{s@f{A~h@fqBzi@r|ApMpRz|@cq@b~@eq@",
            "tzarEbzwcJpNjH|Im[K_EqHjH}NvM",
            "xwdrEtn|cJvzCqwBpi@eZ_Nw_CbMuAqQ{sBmTpD",
            "nsdrE|ztcJhSwdA",
            "zoerE|glcJd|@uHaHylDyo@lE"
        ),
        null)

    fun getMockCountryListData() : List<Country> {
        val countryList: MutableList<Country> = ArrayList()
        val country = Country("AR", "Argentina")
        countryList.add(country)

        return countryList
    }

    fun getMockCityListData() : List<City> {
        val cityList: MutableList<City> = ArrayList()
        cityList.add(city)

        return cityList
    }

    fun getMockCityDetailsData() : City {
        return city
    }
}