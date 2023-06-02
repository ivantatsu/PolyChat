import boto3
import os

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('users')

def lambda_handler(event, context):
    # Obtiene el ID de conexión del evento.
    connection_id = event['requestContext']['connectionId']

    # Realiza una consulta para encontrar el registro con el connection_id.
    response = table.scan(
        FilterExpression='connection_id = :c AND user_id <> :u',
        ExpressionAttributeValues={
            ':c': connection_id,
            ':u': 'temp'
        }
    )
    items = response['Items']

    # Si se encuentra un registro con el connection_id, elimina el campo connection_id.
    if items:
        user_id = items[0]['user_id']
        table.update_item(
            Key={'user_id': user_id},
            UpdateExpression="REMOVE connection_id"
        )

    # Devuelve una respuesta exitosa.
    return {
        'statusCode': 200
    }
