import boto3
import os

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('users')


def lambda_handler(event, context):
    # Obten el ID de conexion del evento.
    connection_id = event['requestContext']['connectionId']
    # Inserta un nuevo registro en la tabla de usuarios.
    table.update_item(
        Key={
            'user_id': 'temp'
        },
        UpdateExpression='set connection_id = :val',
        ExpressionAttributeValues={
            ':val': connection_id
        }
    )

    # Devuelve una respuesta exitosa.
    return {
        'statusCode': 200
    }
