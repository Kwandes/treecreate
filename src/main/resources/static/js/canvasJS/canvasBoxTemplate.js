window.onload = function() {
	var canvas = document.getElementById('canvas');
	var ctx = canvas.getContext('2d');
	
	//sets the apperance
	ctx.strokeStyle = '#000';
	ctx.lineWidth = 1;
	
	//BLACK for everthing under this untill the nexe fillStyle call
	ctx.fillStyle = '#000'
	
	//Core of the black box
	var blackBoxOutline = new Path2D("");	//Stroke the lines and fill the blank spaces
	ctx.stroke(blackBoxOutline);
	ctx.fill(blackBoxOutline);
	
	
	//WHITE filler style for everything under this line
	ctx.fillStyle = '#fff'
	
	var whiteFillMain = new Path2D("");
	ctx.stroke(whiteFillMain);
	ctx.fill(whiteFillMain);


	var whiteFillSmall1 = new Path2D("");
	ctx.stroke(whiteFillSmall1);
	ctx.fill(whiteFillSmall1);

	var whiteFillSmall2 = new Path2D("");
	ctx.stroke(whiteFillSmall2);
	ctx.fill(whiteFillSmall2);

	var whiteFillSmall3 = new Path2D("");
	ctx.stroke(whiteFillSmall3);
	ctx.fill(whiteFillSmall3);

	var whiteFillSmall4 = new Path2D("");
	ctx.stroke(whiteFillSmall4);
	ctx.fill(whiteFillSmall4);

	var whiteFillSmall5 = new Path2D("");
	ctx.stroke(whiteFillSmall5);
	ctx.fill(whiteFillSmall5);

	var whiteFillSmall6 = new Path2D("");
	ctx.stroke(whiteFillSmall6);
	ctx.fill(whiteFillSmall6);

	var whiteFillSmall7 = new Path2D("");
	ctx.stroke(whiteFillSmall7);
	ctx.fill(whiteFillSmall7);

	var whiteFillSmall8 = new Path2D("");
	ctx.stroke(whiteFillSmall8);
	ctx.fill(whiteFillSmall8);

	var whiteFillSmall9 = new Path2D("");
	ctx.stroke(whiteFillSmall9);
	ctx.fill(whiteFillSmall9);

	var whiteFillSmall10 = new Path2D("");
	ctx.stroke(whiteFillSmall10);
	ctx.fill(whiteFillSmall10);

	var whiteFillSmall11 = new Path2D("");
	ctx.stroke(whiteFillSmall11);
	ctx.fill(whiteFillSmall11);

	var whiteFillSmall12 = new Path2D("");
	ctx.stroke(whiteFillSmall12);
	ctx.fill(whiteFillSmall12);

	var whiteFillSmall13 = new Path2D("");
	ctx.stroke(whiteFillSmall13);
	ctx.fill(whiteFillSmall13);

	var whiteFillSmall14 = new Path2D("");
	ctx.stroke(whiteFillSmall14);
	ctx.fill(whiteFillSmall14);

	var whiteFillSmall15 = new Path2D("");
	ctx.stroke(whiteFillSmall15);
	ctx.fill(whiteFillSmall15);

	var whiteFillSmall16 = new Path2D("");
	ctx.stroke(whiteFillSmall16);
	ctx.fill(whiteFillSmall16);

	var whiteFillSmall17 = new Path2D("");
	ctx.stroke(whiteFillSmall17);
	ctx.fill(whiteFillSmall17);

	var whiteFillSmall18 = new Path2D("");
	ctx.stroke(whiteFillSmall18);
	ctx.fill(whiteFillSmall18);

	var whiteFillSmall19 = new Path2D("");
	ctx.stroke(whiteFillSmall19);
	ctx.fill(whiteFillSmall19);

	var whiteFillSmall20 = new Path2D("");
	ctx.stroke(whiteFillSmall20);
	ctx.fill(whiteFillSmall20);

}