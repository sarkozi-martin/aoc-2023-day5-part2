package org.example

class Day5Part2
fun main() {
    val data = Day5Part2::class.java.getResource("/day5data.txt").readText()
    day5Part2(data)
}

fun day5Part2(data: String) {
    data class RangeData(
        val destinationRange: Long,
        val sourceRange: Long,
        val rangeLength: Long,
    )

    var mapSeedToSoil = false
    var mapSoilToFertilizer = false
    var mapFertilizerToWater = false
    var mapWaterToLight = false
    var mapLightToTemperature = false
    var mapTemperatureToHumidity = false
    var mapHumidityToLocation = false

    val seeds = mutableListOf<Long>()
    val seedToSoilMap = mutableListOf<RangeData>()
    val soilToFertilizerMap = mutableListOf<RangeData>()
    val fertilizerToWaterMap = mutableListOf<RangeData>()
    val waterToLightMap = mutableListOf<RangeData>()
    val lightToTemperatureMap = mutableListOf<RangeData>()
    val temperatureToHumidityMap = mutableListOf<RangeData>()
    val humidityToLocationMap = mutableListOf<RangeData>()

    data.split("\r\n").forEach {
        if(it != "") {
            if(it.startsWith("seeds: ")) {
                seeds.addAll(
                    it.split(": ")[1].split(' ').map {
                        it.toLong()
                    }
                )
            }
            if(mapSeedToSoil) {
                it.split(' ').also {
                    seedToSoilMap.addLast(RangeData(it[0].toLong(), it[1].toLong(), it[2].toLong()))
                }
            }
            if(mapSoilToFertilizer) {
                it.split(' ').also {
                    soilToFertilizerMap.addLast(RangeData(it[0].toLong(), it[1].toLong(), it[2].toLong()))
                }
            }
            if(mapFertilizerToWater) {
                it.split(' ').also {
                    fertilizerToWaterMap.addLast(RangeData(it[0].toLong(), it[1].toLong(), it[2].toLong()))
                }
            }
            if(mapWaterToLight) {
                it.split(' ').also {
                    waterToLightMap.addLast(RangeData(it[0].toLong(), it[1].toLong(), it[2].toLong()))
                }
            }
            if(mapLightToTemperature) {
                it.split(' ').also {
                    lightToTemperatureMap.addLast(RangeData(it[0].toLong(), it[1].toLong(), it[2].toLong()))
                }
            }
            if(mapTemperatureToHumidity) {
                it.split(' ').also {
                    temperatureToHumidityMap.addLast(RangeData(it[0].toLong(), it[1].toLong(), it[2].toLong()))
                }
            }
            if(mapHumidityToLocation) {
                it.split(' ').also {
                    humidityToLocationMap.addLast(RangeData(it[0].toLong(), it[1].toLong(), it[2].toLong()))
                }
            }
            if(it.startsWith("seed-to-soil map:")) {
                mapSeedToSoil = true
            }
            if(it.startsWith("soil-to-fertilizer map:")) {
                mapSoilToFertilizer = true
            }
            if(it.startsWith("fertilizer-to-water map:")) {
                mapFertilizerToWater = true
            }
            if(it.startsWith("water-to-light map:")) {
                mapWaterToLight = true
            }
            if(it.startsWith("light-to-temperature map:")) {
                mapLightToTemperature = true
            }
            if(it.startsWith("temperature-to-humidity map:")) {
                mapTemperatureToHumidity = true
            }
            if(it.startsWith("humidity-to-location map:")) {
                mapHumidityToLocation = true
            }
        } else {
            mapSeedToSoil = false
            mapSoilToFertilizer = false
            mapFertilizerToWater = false
            mapWaterToLight = false
            mapLightToTemperature = false
            mapTemperatureToHumidity = false
            mapHumidityToLocation = false
        }
    }

//    println(seeds)
    val seedList = mutableListOf<Pair<Long, Long>>()
    for(i in 0..<seeds.size step 2) {
        seedList.addLast(Pair(seeds[i], seeds[i]+seeds[i+1]))
    }
//    println(seedList)

    fun divideRange(inputList: List<Pair<Long, Long>>, byMap: List<RangeData>): List<Pair<Long, Long>> {
        return inputList.sortedBy { it.first }.map { (start, end) ->
            val x = mutableListOf<Long>()
            /*
             *   Input(start->end)                          ------************--------
             *   A(sourceRange->sourceRange + rangeLength)  ---------******----------- 2 division points(As,As+l)
             *   B(sourceRange->sourceRange + rangeLength)  ----*******************--- 0 division points
             *   C(sourceRange->sourceRange + rangeLength)  ----*********------------- 1 division point(Cs+l)
             *   D(sourceRange->sourceRange + rangeLength)  ---------------*******---- 1 division point(Ds)
             */
            for(a in byMap) {
                if(a.sourceRange < start && a.sourceRange + a.rangeLength > start) { // B or C
                    if(a.sourceRange + a.rangeLength > end) { // B
                    } else { // C
                        x.addAll(listOf(a.sourceRange + a.rangeLength))
                    }
                } else if(a.sourceRange > start && a.sourceRange < end) { // A or D
                    if(a.sourceRange + a.rangeLength > end) { // D
                        x.addAll(listOf(a.sourceRange))
                    } else { // A
                        x.addAll(listOf(a.sourceRange, a.sourceRange + a.rangeLength))
                    }
                }
            }
            if(x.isEmpty()) {
                listOf(Pair(start, end))
            } else {
                val a = x.plus(start).plus(end+1).distinct().sorted()
                val res = mutableListOf<Pair<Long, Long>>()
                for(i in 0..<(a.size-1))
                    res.add(Pair(a[i], a[i+1]-1))
//            println(res)
                res
            }
        }.flatten()
    }

    /**
     * when input range is divided in each intersection points, each subrange is completely moved, or not touched
     */
    fun moveRange(inputList: List<Pair<Long, Long>>, byMap: List<RangeData>): List<Pair<Long, Long>> {
        return inputList.map { (start, end) ->
            val a = byMap.firstOrNull { start >= it.sourceRange && end < (it.sourceRange + it.rangeLength) }
            if (a != null) {
//                println("moving data[$start, $end] to [${start - a.sourceRange + a.destinationRange}, ${end - a.sourceRange + a.destinationRange}]")
                Pair(start - a.sourceRange + a.destinationRange, end - a.sourceRange + a.destinationRange)
            } else {
//                println("returning original data[$start, $end]")
                Pair(start, end)
            }
        }
    }

    fun divideAndMove(inputList: List<Pair<Long, Long>>, byMap: List<RangeData>): List<Pair<Long, Long>> {
        val dividedSeeds = divideRange(inputList, byMap)
//        println(dividedSeeds)
        val movedSeeds = moveRange(dividedSeeds, byMap)
//        println(movedBySoil)
        return movedSeeds
    }

    val bySoil = divideAndMove(seedList, seedToSoilMap)
    val byFertilizer = divideAndMove(bySoil, soilToFertilizerMap)
    val byWater = divideAndMove(byFertilizer, fertilizerToWaterMap)
    val byLight = divideAndMove(byWater, waterToLightMap)
    val byTemperature = divideAndMove(byLight, lightToTemperatureMap)
    val byHumidity = divideAndMove(byTemperature, temperatureToHumidityMap)
    val byLocation = divideAndMove(byHumidity, humidityToLocationMap)

    println(byLocation.map { it.first }.sorted().first())
}
