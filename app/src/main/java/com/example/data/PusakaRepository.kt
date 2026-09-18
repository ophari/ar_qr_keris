package com.example.data

import com.example.model.AcademicResearch
import com.example.model.AnatomyPart
import com.example.model.MeshType
import com.example.model.PusakaItem
import com.example.model.SusQuestion

object PusakaRepository {

    val kerisAnatomyParts = listOf(
        AnatomyPart(
            id = "wilah_luk",
            javaneseName = "Wilah / Bilah Luk 9",
            indonesianName = "Bilah Berkeluk Sembilan",
            description = "Bilah berlekuk sembilan melambangkan kesempurnaan dan kepemimpinan bijaksana. Lekukan dibuat dengan teknik tempa lipat ribuan lapis.",
            philosophy = "Luk 9 (Sanga) melambangkan angka tertinggi, derajat kematangan spiritual dan kewibawaan para pemimpin atau tetua.",
            relativeFocusY = 0.5f
        ),
        AnatomyPart(
            id = "pamor",
            javaneseName = "Pamor Beras Wutah",
            indonesianName = "Motif Pamor Butir Beras Tumpah",
            description = "Garis-garis putih perak berkilau yang tercipta dari campuran bijih besi bumi dengan nikel meteorit (seperti meteorit Prambanan).",
            philosophy = "Beras Wutah bermakna rezeki yang melimpah ruah dan ketenteraman hidup yang dianugerahkan Sang Maha Pencipta.",
            relativeFocusY = 0.3f
        ),
        AnatomyPart(
            id = "ganja",
            javaneseName = "Ganja / Ganyut",
            indonesianName = "Dudukan Bilah Asimetris",
            description = "Bagian penyangga melintang di dasar bilah dengan bentuk asimetris khas keris Nusantara yang mengunci bilah ke bagian pesi.",
            philosophy = "Ganja dan bilah melambangkan persatuan lingga dan yoni, lambang kesuburan, penciptaan alam semesta, dan keseimbangan batin.",
            relativeFocusY = -0.1f
        ),
        AnatomyPart(
            id = "mendak",
            javaneseName = "Mendak & Selut",
            indonesianName = "Cincin Ornamen Pembatas",
            description = "Cincin logam mulia (tembaga, perak, atau emas) berukir butiran teratai (ceplok kembang jeruk) sebagai pembatas antara bilah dan pegangan.",
            philosophy = "Sebagai peredam getaran spiritual serta lambang kehati-hatian agar manusia selalu menjaga batas kehormatan dan etika.",
            relativeFocusY = -0.25f
        ),
        AnatomyPart(
            id = "deder",
            javaneseName = "Deder / Ukiran Hulu",
            indonesianName = "Gagang Kayu Berukir",
            description = "Pegangan tangan yang sedikit membungkuk, biasanya diukir dari kayu langka (Timoho pelet, Kemuning, Cendana) atau gading.",
            philosophy = "Bentuknya yang membungkuk melambangkan kerendahan hati (andhap asor) bahwa semakin tinggi derajat seseorang, ia harus semakin menunduk.",
            relativeFocusY = -0.55f
        ),
        AnatomyPart(
            id = "warangka",
            javaneseName = "Warangka Gayaman & Pendok",
            indonesianName = "Sarung Pelindung Keris",
            description = "Sarung pelindung bagian atas berbentuk buah mangga (Gayaman) untuk suasana santai keraton, berbalut pendok tembaga berukir floral.",
            philosophy = "Warangka manunggal dengan keris melambangkan 'Curiga manjing warangka' atau persatuan jiwa lahir dan batin.",
            relativeFocusY = -0.8f
        )
    )

    val pusakaList = listOf(
        PusakaItem(
            id = "keris_jawa_9",
            name = "Keris Jawa Luk 9 (Pusaka Surakarta)",
            origin = "Jawa Tengah & DI Yogyakarta",
            island = "Jawa",
            era = "Era Mataram Islam (Abad ke-16)",
            category = "Pusaka Sakral & Wibawa",
            unescoRecognition = true,
            unescoYear = "2005",
            description = "Keris berlekuk 9 dengan pamor Beras Wutah dan hulu kayu Timoho pelet alami. Keris diakui UNESCO sebagai Mahakarya Warisan Kemanusiaan Lisan dan Nonbendawi pada tahun 2005.",
            philosophy = "Melambangkan kebijaksanaan, karisma kepemimpinan, dan keseimbangan spiritual mikrokosmos dan makrokosmos.",
            materials = listOf("Besi Bumi", "Baja Karbon", "Nikel Meteorit Prambanan", "Kayu Timoho"),
            meshType = MeshType.KERIS_LUK_9,
            parts = kerisAnatomyParts,
            lengthCm = 46.5f,
            weightGrams = 480,
            trivia = "Keris dibuat oleh seorang Empu melalui ritual tirakat dan penempaan lipatan logam berkisar antara 64 hingga 2048 lapis."
        ),
        PusakaItem(
            id = "mandau_dayak",
            name = "Mandau Dayak (Pusaka Kenyah)",
            origin = "Kalimantan Timur & Tengah",
            island = "Kalimantan",
            era = "Tradisi Leluhur Dayak Kuno",
            category = "Senjata Tradisional & Upacara",
            unescoRecognition = false,
            description = "Pedang pusaka suku Dayak dengan satu bilah cembung tajam dan sisi belakang cekung. Dihiasi ukiran pilinan kuningan dan hulu tanduk rusa berhias rambut dan bulu enggang.",
            philosophy = "Melambangkan keberanian pelindung tanah adat, kehormatan suku, dan hubungan spiritual dengan roh leluhur penjaga hutan.",
            materials = listOf("Besi Mantikei", "Tanduk Rusa", "Kuningan", "Anyaman Rotan Halus"),
            meshType = MeshType.MANDAU_DAYAK,
            parts = emptyList(),
            lengthCm = 65.0f,
            weightGrams = 720,
            trivia = "Mandau asli Dayak sering diselipkan dengan pisau raut kecil (anak mandau) yang digunakan untuk mengukir kayu dan pekerjaan halus."
        ),
        PusakaItem(
            id = "rencong_aceh",
            name = "Rencong Meucugek",
            origin = "Aceh Darussalam",
            island = "Sumatera",
            era = "Kesultanan Samudera Pasai & Aceh (Abad ke-13)",
            category = "Senjata Perjuangan & Martabat",
            unescoRecognition = false,
            description = "Belati tikam legendaris khas Tanah Rencong dengan gagang siku membulat (meucugek). Bentuk lengkungan bilah dan gagang secara filosofis menyerupai tulisan aksara Arab kaligrafi 'Bismillah'.",
            philosophy = "Simbol penegakan keadilan, keteguhan hati berlandaskan keimanan kepada Tuhan Yang Maha Esa, dan martabat pantang menyerah.",
            materials = listOf("Baja Tempa", "Tanduk Kerbau Putih / Kayu Gading", "Perak Berukir"),
            meshType = MeshType.RENCONG_ACEH,
            parts = emptyList(),
            lengthCm = 35.0f,
            weightGrams = 320,
            trivia = "Rencong memiliki 4 strata: Meucugek (hulu melengkung), Meupucok (hulu bermahkota pucuk), Meukuree (berpamor), dan Pudoi (polos)."
        ),
        PusakaItem(
            id = "kujang_sunda",
            name = "Kujang Ciung Prabu Siliwangi",
            origin = "Jawa Barat & Banten",
            island = "Jawa",
            era = "Kerajaan Pajajaran (Abad ke-14)",
            category = "Pusaka Lambang Kesuburan & Kesaktian",
            unescoRecognition = false,
            description = "Bilah melengkung dengan karakteristik 3 hingga 5 lubang mata (mata kujang), punggung berjejer ornamen krawangan dan hulu berbentuk patung burung Ciung bertaji.",
            philosophy = "Kujang melambangkan kekuatan, kemandirian, dan kesuburan pertanian (berakar dari kata Kudhihyang: alat suci pembuka ladang dan pelindung gaib).",
            materials = listOf("Besi Pamor", "Baja Pamor", "Kayu Kemuning Ukir"),
            meshType = MeshType.KUJANG_SUNDA,
            parts = emptyList(),
            lengthCm = 32.0f,
            weightGrams = 410,
            trivia = "Lubang pada Kujang melambangkan tingkatan mandala atau falsafah Trirangku / Pancadarma masyarakat Sunda kuno."
        ),
        PusakaItem(
            id = "celurit_madura",
            name = "Celurit Arit Khas Madura",
            origin = "Pulau Madura & Jawa Timur",
            island = "Madura",
            era = "Legenda Sakera (Abad ke-18)",
            category = "Senjata Ksatria & Pertahanan Diri",
            unescoRecognition = false,
            description = "Senjata bilah sabit melengkung tajam dengan baja lentur yang ditempa khusus. Memiliki pegangan kayu beralur rapat agar kokoh digenggam.",
            philosophy = "Mencerminkan karakter masyarakat Madura yang menjunjung tinggi harga diri, keberanian, dan prinsip 'Lebbi bagus pote tolang e tembhang pote mata' (Lebih baik putih tulang daripada putih mata).",
            materials = listOf("Baja Per Pegas", "Kayu Jati Berulir", "Tembaga Ikat"),
            meshType = MeshType.CELURIT_MADURA,
            parts = emptyList(),
            lengthCm = 58.0f,
            weightGrams = 560,
            trivia = "Lengkungan sabit celurit dirancang secara ergonomis untuk memusatkan momentum pemotongan dengan kecepatan ayunan tinggi."
        ),
        PusakaItem(
            id = "keris_bali_13",
            name = "Keris Bali Luk 13 (Ki Baru Klinting)",
            origin = "Pulau Bali",
            island = "Bali",
            era = "Kerajaan Klungkung & Gelgel (Abad ke-17)",
            category = "Pusaka Keagamaan & Upacara Adat",
            unescoRecognition = true,
            unescoYear = "2005",
            description = "Bilah keris berukuran lebih panjang dan kokoh dari keris Jawa, dengan 13 lekukan dramatis yang dihiasi hulu patung dewa Bhatara Bayu berlapis emas dan tatahan permata mirah delima.",
            philosophy = "Luk 13 melambangkan kestabilan batin dan keagungan spiritual dalam mengarungi dinamika kehidupan duniawi.",
            materials = listOf("Baja Hitam Wulung", "Emas Suasa", "Batu Permata", "Kayu Pelet"),
            meshType = MeshType.KERIS_LUK_13,
            parts = kerisAnatomyParts,
            lengthCm = 54.0f,
            weightGrams = 620,
            trivia = "Di Bali, Keris dipuja pada hari suci Tumpek Landep sebagai sarana introspeksi diri untuk menajamkan ketajaman pikiran budi pekerti."
        )
    )

    // Similar Academic Projects Research Benchmarks for student thesis/project
    val academicReferences = listOf(
        AcademicResearch(
            title = "Aplikasi WebAR Visualisasi 3D Keris Sebagai Media Presentasi Kebudayaan",
            authors = "Tim Peneliti Laboratorium Multimedia",
            institution = "Universitas Brawijaya (UB) Malang",
            year = "2023",
            arTechnology = "WebAR (MindAR & A-Frame)",
            method = "Marker-based Image Tracking",
            summary = "Mengembangkan visualisasi 3D interaktif berbasis web tanpa instalasi aplikasi berat. Pengguna dapat memutar objek dan melihat informasi sejarah.",
            keyFinding = "Meningkatkan engagement generasi muda hingga 78% dibandingkan media buku katalog konvensional."
        ),
        AcademicResearch(
            title = "Pengembangan Aplikasi Augmented Reality Pengenalan Senjata Tradisional Keris Berbasis Android",
            authors = "Fakultas Komunikasi dan Informatika",
            institution = "Universitas Muhammadiyah Surakarta (UMS)",
            year = "2022",
            arTechnology = "Unity 3D + Vuforia SDK",
            method = "Multimedia Development Life Cycle (MDLC)",
            summary = "Pemanfaatan marker gambar kartu budaya untuk memunculkan model 3D keris dengan audio narasi filosofi dan suara gamelan latar.",
            keyFinding = "Skor pengujian Usability SUS rata-rata 83.5 (kategori Sangat Layak / Excellent)."
        ),
        AcademicResearch(
            title = "Media Pembelajaran Interaktif 3D Senjata Tradisional Indonesia Menggunakan AR",
            authors = "Jurusan Teknik Informatika",
            institution = "Institut Teknologi Nasional (ITN) Malang",
            year = "2023",
            arTechnology = "ARCore / Markerless Plane Detection",
            method = "Waterfall SDLC & Blackbox Testing",
            summary = "Eksplorasi penempatan senjata budaya pada permukaan datar (lantai/meja) dengan skala 1:1 realita untuk simulasi museum virtual.",
            keyFinding = "Mode perbandingan skala 1:1 memberikan pemahaman dimensi fisik yang jauh lebih akurat bagi siswa sekolah."
        ),
        AcademicResearch(
            title = "Aplikasi Mobile Edukasi Pengenalan Jenis Keris Tradisional Berbasis Augmented Reality",
            authors = "Fakultas Teknologi Informasi",
            institution = "UNISBANK Semarang",
            year = "2021",
            arTechnology = "Blender 3D + Android Native SDK",
            method = "Prototyping & Kuesioner Pre/Post Test",
            summary = "Fokus pada pengenalan anatomi keris (luk, pamor, warangka) untuk mendukung pelestarian warisan budaya dunia UNESCO.",
            keyFinding = "Nilai pemahaman budaya siswa meningkat 42% setelah 15 menit menggunakan aplikasi interaktif."
        )
    )

    // Standard System Usability Scale (SUS) Questions translated for Thesis testing
    val susQuestions = listOf(
        SusQuestion(1, "Saya merasa akan sering menggunakan aplikasi AR Pusaka Budaya ini dalam belajar sejarah.", true),
        SusQuestion(2, "Saya merasa sistem aplikasi ini terlalu rumit untuk digunakan pemula.", false),
        SusQuestion(3, "Saya merasa aplikasi ini sangat mudah digunakan untuk memvisualisasikan benda 3D.", true),
        SusQuestion(4, "Saya membutuhkan bantuan orang teknis untuk dapat mengoperasikan fitur AR aplikasi ini.", false),
        SusQuestion(5, "Fitur-fitur (Rotasi 3D, AR Kamera, Panduan Anatomi) terintegrasi dengan sangat baik.", true),
        SusQuestion(6, "Banyak hal yang terasa tidak konsisten dalam antarmuka aplikasi ini.", false),
        SusQuestion(7, "Kebanyakan orang dapat belajar menggunakan aplikasi AR ini dengan sangat cepat.", true),
        SusQuestion(8, "Saya merasa antarmuka dan interaksi 3D aplikasi ini sangat canggung atau membingungkan.", false),
        SusQuestion(9, "Saya merasa sangat percaya diri saat menjelajahi artefak senjata dengan aplikasi ini.", true),
        SusQuestion(10, "Saya harus banyak mempelajari hal baru sebelum dapat menikmati visualisasi AR ini.", false)
    )
}
