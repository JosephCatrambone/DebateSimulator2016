package io.xoana.chatbot

/**
 * Created by jcatrambone on 3/28/17.
 */
class Matrix {
	var rows = 0;
	var columns = 0;
	var data = floatArrayOf();

	constructor(rows: Int, columns: Int, value: Float = 0.0f) {
		this.rows = rows;
		this.columns = columns;
		this.data = FloatArray(rows*columns, { _ -> value });
	}

	constructor(rows: Int, columns: Int, fn: (Int) -> Float ) {
		this.rows = rows;
		this.columns = columns;
		this.data = FloatArray(rows*columns, { it -> fn(it) })
	}

	constructor(rows: Int, columns: Int, data: FloatArray) {
		this.rows = rows;
		this.columns = columns;
		this.data = data;
	}

	operator fun get(row:Int, column:Int): Float {
		return this.data[column + row*columns];
	}

	operator fun set(row:Int, column:Int, value:Float) {
		this.data[column + row*columns] = value;
	}

	fun mmul(other: Matrix): Matrix {
		val result = Matrix(this.rows, other.columns);
		for(row in (0..rows-1)) {
			for(column in (0..other.columns-1)) {
				var accumulator = 0.0f;
				for(k in (0..columns-1)) {
					accumulator += this[row,k] * other[k,column];
				}
				result[row, column] = accumulator;
			}
		}
		return result;
	}

	fun transpose(): Matrix {
		val result = Matrix(this.columns, this.rows)
		for(i in 0 until this.rows) {
			for(j in 0 until this.columns) {
				result[j,i] = this[i,j]
			}
		}
		return result
	}

	// Convenience operators.
	operator fun plus(other:Matrix): Matrix {
		return Matrix(this.rows, this.columns, { i -> this.data[i] + other.data[i] })
	}
	operator fun minus(other:Matrix): Matrix {
		return Matrix(this.rows, this.columns, { i -> this.data[i] - other.data[i] })
	}
	operator fun times(other:Matrix): Matrix {
		return Matrix(this.rows, this.columns, { i -> this.data[i] * other.data[i] })
	}
	operator fun div(other:Matrix): Matrix {
		return Matrix(this.rows, this.columns, { i -> this.data[i] / other.data[i] })
	}
	operator fun plus(other:Float): Matrix {
		return Matrix(this.rows, this.columns, { i -> this.data[i] + other })
	}
	operator fun minus(other:Float): Matrix {
		return Matrix(this.rows, this.columns, { i -> this.data[i] - other })
	}
	operator fun times(other:Float): Matrix {
		return Matrix(this.rows, this.columns, { i -> this.data[i] * other })
	}
	operator fun div(other:Float): Matrix {
		return Matrix(this.rows, this.columns, { i -> this.data[i] / other })
	}
	operator fun unaryMinus(): Matrix {
		return Matrix(this.rows, this.columns, { i -> -this.data[i] })
	}

	// Fast in-place operations.
	operator fun plusAssign(other:Matrix) {
		for(i in 0 until this.data.size) {
			this.data[i] += other.data[i]
		}
	}
	operator fun minusAssign(other:Matrix) {
		for(i in 0 until this.data.size) {
			this.data[i] += other.data[i]
		}
	}
	operator fun timesAssign(other:Matrix) {
		for(i in 0 until this.data.size) {
			this.data[i] *= other.data[i]
		}
	}
	operator fun divAssign(other:Matrix) {
		for(i in 0 until this.data.size) {
			this.data[i] /= other.data[i]
		}
	}
	operator fun plusAssign(other:Float) {
		for(i in 0 until this.data.size) {
			this.data[i] += other
		}
	}
	operator fun minusAssign(other:Float) {
		for(i in 0 until this.data.size) {
			this.data[i] += other
		}
	}
	operator fun timesAssign(other:Float) {
		for(i in 0 until this.data.size) {
			this.data[i] *= other
		}
	}
	operator fun divAssign(other:Float) {
		for(i in 0 until this.data.size) {
			this.data[i] /= other
		}
	}
}
