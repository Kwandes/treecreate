window.onload = function() {
	var canvas = document.getElementById('canvas');
	var ctx = canvas.getContext('2d');

	
	//sets the apperance
	ctx.strokeStyle = '#000';
	ctx.lineWidth = 1;
	ctx.fillStyle = '#000';
	//the ridiculous amount of pointers for box 1
	var blackBoxOutline = new Path2D("M 236.9 174.9 C 237.5 181.9 238.1 188.5 238.7 195.2 L 236 196.7 L 218 186 C 211.3 186.9 209 184.8 207.2 173.3 \
		C 198.4 173.3 189.4 173.1 180.5 173.3 C 171.2 173.5 161.8 174.2 152.5 174.5 C 131.4 175 110.2 175.5 89.1 175.8 C 76.9 176 64.7 176 52.5 176.1 \
		C 50.3 176.1 48.2 176.1 44.5 176.1 C 48.2 181.1 50.9 185 53.8 188.8 C 54.5 189.6 55.5 190.2 56.5 190.3 C 65.6 191.2 74.8 200.9 75.4 209.9 C 75.7 \
		214.9 72.1 215.3 69 215.5 C 62.9 215.9 53.6 208.9 53.7 202.8 C 53.9 192.9 45.5 189.4 41.2 181.6 C 38.9 187.5 35.4 191.7 40.5 197.1 C 42 198.7 \
		41.3 203.6 40.1 206.3 C 39.5 207.7 34.1 208.8 33.3 207.9 C 31.3 205.8 30.2 202.4 29.9 199.4 C 29.6 196.9 30.6 194.2 31.4 191.6 C 33.3 185.9 \
		30.3 181.6 28.6 176.4 C 25.6 167.4 23.4 157 24.9 147.9 C 29 123.5 32.2 99.2 31 74.4 C 30.8 69.8 29.7 65.2 29 60.6 C 28.6 58.7 28.3 56.7 28.2 \
		54.7 C 28.1 43.7 28.1 43.7 22.2 33.8 C 17.9 26.6 17.9 20.2 22.4 16 C 24.4 14.1 25.8 13 28.9 15.7 C 36.5 22.2 39.5 30.1 38.9 39.7 H 151.8 \
		C 142.4 30.4 135.8 20.5 139.1 5 L 157.3 14.3 L 177.6 9.7 C 183.2 19 182.5 25.7 175.2 39.5 C 191.4 40.8 206.8 45.9 223.7 41.5 C 236.8 38.1 \
		251.1 38.9 264.9 39 C 277.8 39.1 290.7 40.7 303.5 41.6 C 317.7 42.6 331.9 43.7 346.1 44.7 C 348.7 44.9 351.4 44.7 354.2 44.7 C 355 40.2 355.1 \
		35.8 356.7 32.1 C 358.4 28.3 361 25 364.3 22.6 C 366.2 21.2 370.8 21.3 372.6 22.8 S 375.4 28.6 374.7 31 C 373.4 35.4 373.3 38.5 376.5 42.3 C \
		381.2 48 378.8 55.3 371.9 58.1 C 369.3 59.1 366.4 59.4 363.5 60.1 V 119.6 C 363.4 132.1 368.3 111.1 376.8 117.8 C 377.1 114.6 377.7 112.3 377.4 \
		110.1 C 376.2 101.2 380.6 93.1 388.1 90.1 C 392.7 88.2 394.4 89.9 394.7 94.2 C 394.9 97.1 394.9 100 394.7 102.9 C 394.4 105.7 393.7 108.5 393.2 \
		111.3 C 399.9 114.2 400.5 117.6 394.4 122.2 C 391.7 124.2 387.7 126.2 384.7 125.7 C 376 124.4 371.5 130.1 368.6 135.9 C 364.5 144.2 364.4 153 \
		362.5 162 C 360.5 171.2 355.8 176 343.9 175.9 C 329.2 175.8 314.5 174.7 299.9 174.5 C 285 174.3 270.1 174.7 255.2 174.8 C 248.7 174.9 242.3 174.9 \
		236.9 174.9 Z M 32.9 165.7 C 39.2 165.7 43.4 165.8 47.7 165.7 C 85.5 165.2 123.3 164.5 161.1 164.4 C 181.8 164.3 202.5 165.6 223.2 165.7 C 262 165.9 \
		300.9 165.6 339.7 165.9 C 347 165.9 351.1 162.8 352.9 156.2 C 354 152.8 354.6 149.3 354.7 145.8 C 354.9 117.6 354.8 89.5 354.9 61.4 C 354.9 57.9 \
		354 55.6 349.8 55.2 C 336.8 54 323.8 51.2 310.7 51 C 280.8 50.7 251 51.8 221.1 52 C 210.3 52.1 199.4 53.7 188.7 49.6 C 184.3 47.9 179.1 48 174.2 \
		47.9 C 161.3 47.8 148.3 48 135.4 48.2 C 112.2 48.6 89.1 48.9 65.9 49.5 C 56.6 49.7 47.3 50.7 37.1 51.3 C 37.5 55.1 37.8 58.7 38.1 62.1 L 51.3 \
		54.8 C 55.3 64.1 53.4 72.8 46 77.5 C 42 80 41.4 82.7 41.5 86.7 C 41.8 99.8 41 112.7 37.7 125.7 C 34.5 138.3 34.4 151.6 32.9 165.7 L 32.9 165.7 \
		Z M 162.9 38 C 164.4 26.1 154.3 13.9 142.5 11.5 C 140.9 20.2 146.9 31.6 153.7 33.8 C 152.6 30.9 151.3 27.6 150 24.2 L 151.3 23.7 L 162.9 38 Z M \
		167.4 38.9 C 177.4 33.4 177.3 25.5 177 18 C 177 16.6 173.7 14.1 172.3 14.3 C 168.4 14.9 164.7 16.6 160.6 18 L 165.3 25.1 L 170.8 23 C 169.6 28.4 \
		168.6 33 167.4 38.9 L 167.4 38.9 Z M 361.4 47.2 L 363.7 48.1 C 366.1 41.1 368.5 34.1 371.2 26.2 C 361.2 27.5 359.1 36.7 361.4 47.2 L 361.4 47.2 Z \
		M 26.5 18.7 C 22.4 25.6 26.3 35.6 33.5 38 C 35.6 30.2 30.3 25.1 26.5 18.7 Z M 383.1 112.4 C 389.6 109.6 392 102.2 388.7 94.7 C 381.6 99 382.4 105.7\
		 383.1 112.4 L 383.1 112.4 Z M 224.8 178.6 L 222.6 180.1 C 225.1 183.4 227.4 186.9 230.2 189.8 C 230.8 190.4 235 188.4 235 188.3 C 234 184.2 232.7 \
		180.3 231.5 176.3 L 229.4 177 C 229.9 179.4 230.5 181.8 231 184.2 L 229.8 184.9 C 228.1 182.8 226.5 180.7 224.8 178.6 Z M 70.7 210.6 C 69.3 203.6 \
		65.8 199.5 58.3 200.9 C 60.4 207.7 62.2 209.1 70.7 210.6 L 70.7 210.6 Z M 220.8 172.8 C 217.6 173.4 214.8 173.5 212.5 174.6 C 211.9 174.9 212 179.2 \
		213 179.9 C 215 181.3 217.8 181.4 220.8 182.2 V 172.8 Z M 371.5 44.1 C 368.6 47.8 366.1 50.9 363.3 54.4 C 373.3 54 375.6 50.9 371.5 44.1 Z M 40.6 \
		73.1 L 44 74.6 L 49.4 62.8 L 46.6 61.5 C 44.6 65.3 42.6 69.2 40.6 73.1 L 40.6 73.1 Z");		
	//Stroke the lines and fill the blank spaces
	ctx.stroke(blackBoxOutline);
	ctx.fill(blackBoxOutline);
	//override color to be white for the filler
	ctx.fillStyle = '#ff0'
	var whiteFillMain = new Path2D("M32.9,165.7c1.5-14.1,1.6-27.4,4.7-39.9c3.3-13,4.1-25.8,3.8-39c-0.1-4,0.5-6.7,4.5-9.2\
		c7.5-4.7,9.3-13.4,5.3-22.7l-13.2,7.3c-0.3-3.5-0.7-7-1-10.8c10.2-0.7,19.5-1.6,28.8-1.8c23.2-0.6,46.3-0.9,69.5-1.3\
		c12.9-0.2,25.9-0.4,38.8-0.3c4.9,0,10.1,0,14.5,1.7c10.7,4.1,21.6,2.5,32.4,2.4c29.9-0.2,59.7-1.3,89.6-1c13,0.1,26,2.9,39.1,4.2\
		c4.3,0.4,5.2,2.7,5.1,6.2c0,28.1,0,56.3-0.2,84.4c-0.2,3.5-0.8,7-1.8,10.4c-1.8,6.6-5.9,9.7-13.2,9.7c-38.8-0.2-77.7,0.1-116.5-0.2\
		c-20.7-0.1-41.4-1.4-62.1-1.3c-37.8,0.1-75.6,0.9-113.4,1.3C43.5,165.7,39.2,165.7,32.9,165.7z");
	ctx.stroke(whiteFillMain);
	ctx.fill(whiteFillMain);

	var whiteFillSmall1 = new Path2D("M32.9,165.7c1.5-14.1,1.6-27.4,4.7-39.9c3.3-13,4.1-25.8,3.8-39c-0.1-4,0.5-6.7,4.5-9.2\
		c7.5-4.7,9.3-13.4,5.3-22.7l-13.2,7.3c-0.3-3.5-0.7-7-1-10.8c10.2-0.7,19.5-1.6,28.8-1.8c23.2-0.6,46.3-0.9,69.5-1.3\
		c12.9-0.2,25.9-0.4,38.8-0.3c4.9,0,10.1,0,14.5,1.7c10.7,4.1,21.6,2.5,32.4,2.4c29.9-0.2,59.7-1.3,89.6-1c13,0.1,26,2.9,39.1,4.2\
		c4.3,0.4,5.2,2.7,5.1,6.2c0,28.1,0,56.3-0.2,84.4c-0.2,3.5-0.8,7-1.8,10.4c-1.8,6.6-5.9,9.7-13.2,9.7c-38.8-0.2-77.7,0.1-116.5-0.2\
		c-20.7-0.1-41.4-1.4-62.1-1.3c-37.8,0.1-75.6,0.9-113.4,1.3C43.5,165.7,39.2,165.7,32.9,165.7z");
	ctx.stroke(whiteFillSmall1);
	ctx.fill(whiteFillSmall1);
	
	var whiteFillSmall2 = new Path2D("M162.9,38l-11.7-14.2l-1.3,0.5c1.3,3.4,2.6,6.7,3.7,9.6c-6.8-2.3-12.8-13.6-11.2-22.3\
		C154.3,14,164.4,26.1,162.9,38z");
	ctx.stroke(whiteFillSmall2);
	ctx.fill(whiteFillSmall2);
	
	var whiteFillSmall3 = new Path2D("M167.4,39c1.3-6,2.2-10.5,3.4-15.9l-5.5,2.1l-4.7-7.1c4.1-1.4,7.8-3.2,11.7-3.8c1.4-0.2,4.7,2.4,4.7,3.7\
	C177.2,25.6,177.4,33.4,167.4,39z");
	ctx.stroke(whiteFillSmall3);
	ctx.fill(whiteFillSmall3);

	var whiteFillSmall4 = new Path2D("M361.4,47.2c-2.3-10.5-0.1-19.8,9.8-21c-2.7,7.9-5.1,14.9-7.5,21.9L361.4,47.2z");
	ctx.stroke(whiteFillSmall4);
	ctx.fill(whiteFillSmall4);

	var whiteFillSmall5 = new Path2D("M26.5,18.7c3.9,6.5,9.2,11.5,7,19.3C26.3,35.6,22.4,25.6,26.5,18.7z");
	ctx.stroke(whiteFillSmall5);
	ctx.fill(whiteFillSmall5);

	var whiteFillSmall6 = new Path2D("M383,112.4c-0.6-6.7-1.5-13.5,5.6-17.7C391.9,102.3,389.6,109.7,383,112.4z");
	ctx.stroke(whiteFillSmall6);
	ctx.fill(whiteFillSmall6);

	var whiteFillSmall7 = new Path2D("M224.8,178.6c1.6,2.1,3.3,4.2,5,6.2l1.2-0.7c-0.5-2.4-1.1-4.8-1.6-7.2l2.1-0.7c1.2,4,2.6,7.9,3.5,12\
	c0,0.2-4.2,2.1-4.8,1.5c-2.9-2.9-5.1-6.4-7.6-9.7L224.8,178.6");
	ctx.stroke(whiteFillSmall7);
	ctx.fill(whiteFillSmall7);

	var whiteFillSmall8 = new Path2D("M70.7,210.6c-8.5-1.6-10.3-2.9-12.4-9.7C65.8,199.5,69.3,203.6,70.7,210.6z");
	ctx.stroke(whiteFillSmall8);
	ctx.fill(whiteFillSmall8);

	var whiteFillSmall9 = new Path2D("M220.8,172.9v9.4c-3-0.8-5.8-0.9-7.8-2.3c-1-0.7-1.1-5-0.5-5.3C214.8,173.6,217.5,173.5,220.8,172.9z");
	ctx.stroke(whiteFillSmall9);
	ctx.fill(whiteFillSmall9);

	var whiteFillSmall10 = new Path2D("M371.5,44.1c4.2,6.8,1.9,9.9-8.2,10.3C366.1,50.9,368.5,47.8,371.5,44.1z");
	ctx.stroke(whiteFillSmall10);
	ctx.fill(whiteFillSmall10);

	var whiteFillSmall11 = new Path2D("M40.6,73.1c2-3.9,4-7.8,6-11.6l2.8,1.3L44,74.6L40.6,73.1z");
	ctx.stroke(whiteFillSmall11);
	ctx.fill(whiteFillSmall11);
}