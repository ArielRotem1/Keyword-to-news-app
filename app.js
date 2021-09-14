async function getYnetDataToFile(){
    try{
        await console.log("in getYnetDataToFile");

        const browser = await puppeteer.launch();
        const page = await browser.newPage();
        page.setDefaultTimeout(1000000);
        await page.goto('https://www.ynet.co.il/home/0,7340,L-8,00.html', {waitUntil: "networkidle0"});
        //await page.screenshot({ path: 'example.png' });
        
        let links = await page.evaluate(() => {
            let group = document.querySelector("#BottomHeaderArea > div.popUpmenu.closed > div.linksGroups").getElementsByClassName("group")[1];
            let listLinks = group.getElementsByTagName("li");
            let links = [];
            for(let i = 0; i < listLinks.length; i++){
                links.push(listLinks[i].getElementsByTagName("a")[0].href);
            }
            return links;
        })

        let articles = [];

        for(let i = 1; i < links.length; i++){
            if(links[i].includes("pplus") || links[i].includes("podcasts") || links[i].includes("frogi") || links[i].includes("weather")
            || links[i].includes("laisha") || links[i].includes("architecture") || links[i].includes("fashion")){
                continue;
            }

            await page.goto(links[i], {waitUntil: "networkidle0"});
            
            try{
                var arts = await page.evaluate(() => {
                    let articles = [];
                    var today = new Date();
                    var todaydd = String(today.getDate()).padStart(2, '0');
                    var todaymm = String(today.getMonth() + 1).padStart(2, '0');
                    var todayyy = String(today.getFullYear()).substring(2);

                    var todayString = todaydd + '.' + todaymm + '.' + todayyy;

                    let index = 0;
                    while(document.querySelector("body > div:nth-child("+index+") > div > div > div.RelativeElementsContainer.site_page_root") == null) index++;
                    let articlesContainer = document.querySelector("body > div:nth-child("+index+") > div > div > div.RelativeElementsContainer.site_page_root");
                    let slotViews = articlesContainer.getElementsByClassName("slotView");
                    for(let i = 0; i < slotViews.length; i++){
                        let slotView = slotViews[i];
                        if(slotView.classList.length == 1){
                            let aSlot = slotView.getElementsByTagName("a");
                            if(aSlot.length > 2) continue;
                            let date = slotView.getElementsByClassName("DateDisplay")[0];
                            if(date != undefined) date = date.innerText;
                            else date = todayString;

                            if(date.includes("|")){
                                //אתמול
                                var today2 = new Date(today);
                                today2.setDate(today2.getDate() - 1);

                                var dd = String(today2.getDate()).padStart(2, '0');
                                var mm = String(today2.getMonth() + 1).padStart(2, '0');
                                var yy = String(today2.getFullYear()).substring(2);

                                date = dd + '.' + mm + '.' + yy;
                            }
                            else if(date.includes(":")){
                                date = todayString;
                            }

                            //check if month has passed
                            let dateParts = date.split('.');
                            let dateObj = new Date(parseInt("20" + dateParts[2]), parseInt(dateParts[1]) - 1, parseInt(dateParts[0]));
                            let numberOfDaysDiff = Math.floor(Math.abs(today.getTime() - dateObj.getTime()) / (24 * 60 * 60 * 1000));

                            if(numberOfDaysDiff > 30){
                                continue;
                            }

                            for(let j = 0; j < aSlot.length; j++){
                                if(aSlot[j].getElementsByTagName("img").length == 0){
                                    let art = aSlot[j];
                                    if(art.getElementsByClassName("slotTitle").length != 0){
                                        let link = art.href;
                                        let title = art.getElementsByClassName("slotTitle")[0].innerText;
                                        articles.push({link, title, date});
                                    }
                                    else{
                                        articles.push({link: art.href, title: art.innerText, date});
                                    }
                                }
                            }
                        }
                        //else console.log("ad");
                    }

                    return articles;
                })
            }
            catch(err){
                await console.log("error in getting article from Ynet, error: " + err);
            }

            articles.push(...arts);
        }

        await browser.close();

        await console.log("out getYnetData");
        await writeJSONFile("ynetData", articles);
        await console.log("finish writing file!");
        await console.log();
    }
    catch(error){
        await console.log("error in getYnetDataToFile, error: " + err);
    }
}

async function getN12DataToFile(){
    try{
        await console.log("in getN12DataToFile");

        const browser = await puppeteer.launch();
        const page = await browser.newPage();
        page.setDefaultTimeout(1000000);
        await page.goto('https://www.n12.co.il/', {waitUntil: "networkidle0"});
        //await page.screenshot({ path: 'example.png' });

        let linksToTypes = await page.evaluate(() => {
            let linksToTypes = [];
            let menu = document.querySelector("body > nav").getElementsByTagName("li")[0].getElementsByTagName("li");
            for(let i = 1; i < menu.length; i++){
                let link = menu[i].getElementsByTagName("a")[0].href;
                linksToTypes.push(link);
            }

            return linksToTypes;
        });

        let articles = [];

        for(let i = 0; i < linksToTypes.length; i++){
            await page.goto(linksToTypes[i], {waitUntil: "networkidle0"});

            try{
                var arts = await page.evaluate(() => {
                    let arts = [];
                    var today = new Date();

                    var dd = String(today.getDate()).padStart(2, '0');
                    var mm = String(today.getMonth() + 1).padStart(2, '0');
                    var yy = String(today.getFullYear()).substring(2);

                    if(document.querySelector("body > div.main-wrap > main > section.content-wrap > section.mainItem.mainItem6.full") != null){
                        let mainArts = document.querySelector("body > div.main-wrap > main > section.content-wrap > section.mainItem.mainItem6.full").getElementsByTagName("li");
                        for(let i = 0; i < mainArts.length; i++){
                            let p = mainArts[i].getElementsByTagName("p")[0];
                            let a = p.getElementsByTagName("a")[0];
                            let title = a.innerText;
                            let link = a.href;
                            let date = p.getElementsByTagName("small")[0].innerText.split("| ")[1];

                            if(date == undefined){
                                continue;
                            }
                            
                            if(date.includes(":")){
                                date = dd + '.' + mm + '.' + yy;
                            }

                            //check if month has passed
                            let dateParts = date.split('.');
                            let dateObj = new Date(parseInt("20" + dateParts[2]), parseInt(dateParts[1]) - 1, parseInt(dateParts[0]));
                            let numberOfDaysDiff = Math.floor(Math.abs(today.getTime() - dateObj.getTime()) / (24 * 60 * 60 * 1000));

                            if(numberOfDaysDiff > 30){
                                continue;
                            }

                            arts.push({link, title, date});
                        }
                    
                        let regularArts = document.querySelector("body > div.main-wrap > main > section.content-wrap > section.regular.content.colx2").getElementsByTagName("li");
                        for(let i = 0; i < regularArts.length; i++){
                            let p = regularArts[i].getElementsByTagName("p")[0];
                            let a = p.getElementsByTagName("a")[0];
                            let title = a.innerText;
                            let link = a.href;
                            let date = p.getElementsByTagName("small")[0].innerText.split("| ")[1];

                            if(date == undefined){
                                continue;
                            }

                            if(date.includes(":")){
                                date = dd + '.' + mm + '.' + yy;
                            }

                            //check if month has passed
                            let dateParts = date.split('.');
                            let dateObj = new Date(parseInt("20" + dateParts[2]), parseInt(dateParts[1]) - 1, parseInt(dateParts[0]));
                            let numberOfDaysDiff = Math.floor(Math.abs(today.getTime() - dateObj.getTime()) / (24 * 60 * 60 * 1000));

                            if(numberOfDaysDiff > 30){
                                continue;
                            }

                            arts.push({link, title, date});
                        }
                    }

                    return arts;
                });
            }
            catch(err){
                await console.log("error in getting article from N12, error: " + err);
            }

            articles.push(...arts);
        }

        await browser.close();

        await console.log("out getN12Data");
        await writeJSONFile("n12Data", articles);
        await console.log("finish writing file!");
        await console.log();
    }
    catch(error){
        await console.log("error in getN12DataToFile, error: " + err);
    }
}

const puppeteer = require("puppeteer");
const express = require("express");
var server = express();
const fs = require('fs');

const hostname = '127.0.0.1';
const port = 3000;

server.get('/', function (req, res) {
    res.send("hello world");
})

server.get('/getDataYnet', async function (req, res) {
    await console.log("in getDataYnet");

    let fileName = "ynetData";

    await fs.readFile("./" + fileName + ".json", "utf8" , (err, data) => {
        if (err) {
            console.error("readJSONFile error: " + err);
            return;
        }
        console.log("readJSONFile data: ")
        let parsedData = JSON.parse(data);
        //console.log(parsedData);

        console.log("finish reading file!");
        console.log();

        res.send(parsedData);
    });
});

server.get('/getDataN12', async function (req, res) {
    await console.log("in getDataN12");

    let fileName = "n12Data";

    await fs.readFile("./" + fileName + ".json", "utf8" , (err, data) => {
        if (err) {
            console.error("readJSONFile error: " + err);
            return;
        }
        console.log("readJSONFile data: ")
        let parsedData = JSON.parse(data);
        //console.log(parsedData);

        console.log("finish reading file!");
        console.log();

        res.send(parsedData);
    });
});

server.listen(port, hostname, () => {
    getDataToFile();
    setInterval(getDataToFile, 3600000);
    console.log(`Server running at http://${hostname}:${port}/`);
});

async function writeJSONFile(fileName, fileContent){
    await fs.writeFile("./"+ fileName + ".json", JSON.stringify(fileContent), function(err) {
        if(err) {
            return console.log("writeJSONFile error: " + err);
        }
        console.log("writeJSONFile has been written!");
        console.log();
    });
}

function getDataToFile(){
    getYnetDataToFile();
    getN12DataToFile();
}




/*
const server = http.createServer(async (req, res) => {
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/plain');
    res.end('Hello 2World\n');
});

//taskkill /F /IM node.exe
*/

