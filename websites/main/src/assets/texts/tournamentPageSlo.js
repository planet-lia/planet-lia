import React from 'react'

const tournamentPageSlo = {
  titleTour: "Slovenski Lia turnir 2019",
  txtBanner: [
    <span key="0">Si študent ali dijak iz Slovenije in znaš vsaj malo
      programirati?</span>,
    <br key="1"/>,
    <span key="2">Pridruži se Lia in se poteguj za nagrade!</span>
  ],
  tourDate: "18. Feb - 14. Mar",
  cdDays: "Dnevi",
  cdHours: "Ure",
  cdMinutes: "Minute",
  cdSeconds: "Sekunde",
  txtLive: "Kvalifikacije se končujejo!",
  btnRegisterEarly: "Pridruži se!",
  lnkWhat: "Kaj je Lia?",
  bnrFBTxt1: (<span><strong>Rezerviraj vstopnico</strong></span>),
  bnrFBTxt2: "in preberi več o dogodku.",

  bnrFinalsTitle: "Finale",
  bnrFinalsTxt: (<span>Slovenski Lia turnir 2019 je zaključen!<br/>Oglej si rezultate finalnega kroga.</span>),
  bnrFinalsBtn: "Rezultati finalov",

  titleWant: "Bi se rad zabaval in premagal prijatelje v programiranju?",
  txtWant: "Izberi svoj najljubši programski jezik in uporabi že pripravljene začetne bote. Začneš lahko v le nekaj minutah.",
  btnLeaderboard: "Lestvica",
  btnWatch: "Glej igre",
  titleCheck: "Preveri stanje na lestvici.",
  txtCheck: "Sledi rezultatom svojih sošolcev in prijateljev ali pa si oglej igre najboljših igralcev Lia.",
  titleGetStarted: "Začni v le nekaj minutah.",
  txtGetStarted: "Preizkusi igro Lia preko spletnega urejevalnika ali pa si za popolno Lia izkušnjo prenesi Lia-SDK in začni razvijati svojega bota lokalno.",
  btnEditor: "Spletni urejevalnik",
  btnGetStarted: "Razvijaj lokalno",

  titleAgenda: "Spored",
  txtAgenda1: [
    <span key="0">Pridruži se spletni lestvici in tekmuj proti ostalim
      udeležencem.</span>,
    <br key="1"/>,
    <span key="2">Prijave odprte za vse slovenske študente in dijake!</span>
  ],
  txtAgenda2: "Zadnje izboljšave botov najboljših 16 udeležencev, ki so se uvrstili na zaključni turnir.",
  txtAgenda3: [
    <strong key="0">Zaključni turnir</strong>,
    <span key="1"> ob 16:00 na Fakulteti za računalništvo in informatiko Univerze v
      Ljubljani v predavalnici P22. Hrana, pijača in zabava zagotovljena!
    </span>
  ],
  titlePrizes: "Nagrade",
  txtPrize1: "1. mesto na zaključnem turnirju",
  txtSubPrize1: "PlayStation 4 in FIFA 19 + pripravništvo",
  txtSponsor1: "Styliff Tech",
  txtPrize2: "2. mesto na zaključnem turnirju",
  txtSubPrize2: "Panoramski polet nad Triglavom za tri osebe",
  txtPrize3: "3. mesto na zaključnem turnirju",
  txtSubPrize3: "Brezžične slušalke Sennheiser HD 4.50 BT",
  txtPrize4: "Vodilni po 1., 2. in 3. tednu",
  txtSubPrize4: "3x JBL GO bluetooth zvočnik",
  txtPrize5: "Najboljša igra turnirja",
  txtSubPrize5: "Žepni dron UGO Zephir",
  txtPrize6: "Nagrade za gledalce",
  txtSubPrize6: "5x 20€ BIG BANG darilni bon",
  txtPrize7: "Najboljše uvrščeni dijak",
  txtSubPrize7: "Razer Cynosa PRO in Deathadder 2000",
  txtSponsor7: "Študentski svet FRI",
  txtSponBy: "sponzorira",
  titleRules: "Pravila",
  txtRules: "Natančnejše informacije in pravila o turnirju lahko prebereš ",
  linkRules: "tukaj",

  titleSponsors: "Sponzorji in partnerji",
  txtSponsors: "Turnir ne bi bil mogoč brez naših sponzorjev in partnerjev.",

  txtRlsMain: "To so pravila slovenskega Lia turnirja 2019.",
  titleRls1: "Lia verzija",
  txtRls1: "Na tem turnirju bo v uporabi Lia-SDK verzija v1.0.x. Ker je verzija povsem nova, bomo v kolikor bo to potrebno, v prvih dneh tekmovanja nekoliko prilagodili parametre igre.",
  titleRls2: "Nagrade",
  txtRls2: "Nagrade za vodilnega po 1., 2. in 3. tednu bodo podeljene po sledečih pravilih. Prvi zmagovalec bo igralec, ki bo vodilni na lestvici 24. februarja ob 20:00, drugi " +
  "3. marca ob 20:00 in tretji 10. marca ob 20:00. Kljub temu, da bo nalaganje botov na spletno stran onemogočeno 9. marca ob 20:00, se bodo igre odvijale do 10. marca do 20:00. " +
  "V kolikor je igralec v predhodnjem tednu že dobil nagrado vodilnega, je nagrada podeljena naslednje uvrščenemu igralcu.",
  titleRls3: "Diskvalifikacija",
  txtRls3: "Uporaba večih računov je prepovadana. Prepovedana je tudi uporaba kode drugega igralca. Vsaka taka dejavnost bo kaznovana z diskvalifikacijo. Enako velja za poizkuse napadov na Lia strežnike in za ostale škodoželjne poizkuse, ki bi lahko škodili poteku turnirja. Smo zgolj študentje, ki si želimo organizirati zabaven turnir za programersko skupnost, tako da bodite prizanesljivi. :)",
  titleRls4: "Spletna lestvica (18. februar - 9. marec)",
  txtRls4: "V tem času bo preko spleta na voljo Lia lestvica. Vsi slovenski študentje in dijaki se bodo lahko v tem času registrirali in nalagali svoje bote. Naložen bot bo avtomatično tekmoval proti ostalim igralcem na lestvici in bo na njej uvrščen glede na njegovo kvaliteto. Za računanje ranka botov bomo uporabljali knjižnico TrueSkill. Po 9. marcu ob 20:00 bomo onemogočili objavljanje novih verzij botov, ponastavili lestvico in v naslednjih 24 urah generirali veliko število iger. Končni vrstni red lestvice bo določen v 10. marca ob 20:00. Najboljših 16 igralcev se bo uvrstilo na končni turnir. Kmalu po zaključku kvalifikacijske lestvice, bodo znani tekmeci v prvih krogih finalnega turnirja.",
  titleRls5: "Priprave na zaključni turnir  (10. marec - 13. marec)",
  txtRls5: "Najboljših 16 igralcev spletne lestvice bo uvrščenih na zaključni turnir. V vmesnem času bodo lahko vsi uvrščeni igralci med 10. marcem od 20:00 in polnočjo 13. marca posodabljali verzijo bota, ki ga bodo uporabili na tekmovanju. Nasprotniki na končnem turnirju bodo znani takoj ob zaključku spletne lestvice, tako da se bodo igralci lahko pripravili na točno določene igralce, ki jih čakajo na zaključnem turnirju. V tem času lestvica ne bo delovala!",
  titleRls6: "Zaključni turnir (14. marec)",
  txtRls6: (
    <span>Zaključni turnir bo potekal na Fakulteti za računalništvo in
      informatiko (FRI) Univerze v Ljubljani na naslovu Večna pot 113, 1000
      Ljubljana ob 16:00 v učilnici P22. Na zaključnem turnirju bo tekmovalo
      16 najboljših igralcev spletne lestvice v formatu best of
      5. <a href="https://www.facebook.com/events/2543198445721481/">Program</a> in
      ostale podrobnosti si lahko preberete na strani dogodka. Prijavite se
      lahko tako igralci kot gledalci.
      <br/>
      Ekipa Lia bo predvajala vse igre na velikem platnu, po
      končanem turnirju pa bodo vse igre dostopne tudi preko uradne spletne
      strani. Na turnirju bo poskrbljeno tako za igralce kot za gledalce.
    </span>
  ),
  btnBack: "Nazaj"
}

export default tournamentPageSlo;
